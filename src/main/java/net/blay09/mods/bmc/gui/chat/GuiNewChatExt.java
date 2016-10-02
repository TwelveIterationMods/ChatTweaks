package net.blay09.mods.bmc.gui.chat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksConfig;
import net.blay09.mods.bmc.api.chat.IChatChannel;
import net.blay09.mods.bmc.api.chat.IChatMessage;
import net.blay09.mods.bmc.api.chat.MessageStyle;
import net.blay09.mods.bmc.api.event.ClientChatEvent;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.blay09.mods.bmc.chat.TextRenderRegion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuiNewChatExt extends GuiNewChat {

	public static class WrappedChatLine {
		private final int timeCreated;
		private final IChatMessage message;
		private final TextRenderRegion[] regions;
		private final boolean alternateBackground;

		public WrappedChatLine(int timeCreated, IChatMessage message, TextRenderRegion[] regions, boolean alternateBackground) {
			this.timeCreated = timeCreated;
			this.message = message;
			this.regions = regions;
			this.alternateBackground = alternateBackground;
		}
	}

	private static final Matcher DEFAULT_SENDER_MATCHER = Pattern.compile("(<[^>]+>)").matcher("");
	private static final int START_ID = 500;

	protected final Minecraft mc;
	private FontRenderer fontRenderer;
	private final AtomicInteger chatLineCounter = new AtomicInteger(START_ID);
	private final Map<Integer, ChatMessage> chatLines = Maps.newHashMap();
	private final Set<Integer> unreadMessages = Sets.newHashSet();
	private final List<ChatChannel> channels = Lists.newArrayList();
	private final List<WrappedChatLine> wrappedChatLines = Lists.newArrayList();

	private ChatChannel activeChannel;
	private boolean alternateBackground;

	public GuiNewChatExt(Minecraft mc) {
		super(mc);
		this.mc = mc;
		this.fontRenderer = mc.fontRendererObj;
		MinecraftForge.EVENT_BUS.register(this);
	}

	private final List<ChatChannel> tmpMessageCandidates = Lists.newArrayList();

	@Override
	public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
		if (chatLineId == 0) {
			chatLineId = chatLineCounter.incrementAndGet();
		}
		addChatMessage(new ChatMessage(chatLineId, chatComponent));
	}

	public IChatMessage addChatMessage(ITextComponent chatComponent, IChatChannel channel) {
		ChatMessage chatMessage = new ChatMessage(chatLineCounter.incrementAndGet(), chatComponent);
		channel.addManagedChatLine(chatMessage);
		chatMessage.setExclusiveChannel(channel);
		if (channel != activeChannel) {
			unreadMessages.add(chatMessage.getId());
		}
		addChatMessageForDisplay(chatMessage, channel);
		return chatMessage;
	}

	public void addChatMessage(ChatMessage chatMessage) {
		String unformattedText = chatMessage.getChatComponent().getUnformattedText();
		tmpMessageCandidates.clear();
		for (ChatChannel channel : channels) {
			if (channel.messageMatches(unformattedText)) {
				if (channel.isExclusive()) {
					tmpMessageCandidates.clear();
					tmpMessageCandidates.add(channel);
					chatMessage.setExclusiveChannel(channel);
					break;
				}
				tmpMessageCandidates.add(channel);
			}
		}
		for (ChatChannel channel : tmpMessageCandidates) {
			ChatMessage newChatMessage = channel.addChatLine(chatMessage);
			addChatMessageForDisplay(newChatMessage, channel);
		}
	}

	private void addChatMessageForDisplay(IChatMessage chatMessage, IChatChannel channel) {
		if (channel != activeChannel) {
			unreadMessages.add(chatMessage.getId());
		}
		switch (channel.getMessageStyle()) {
			case Chat:
				int chatWidth = MathHelper.floor_float((float) this.getChatWidth() / this.getChatScale());
				List<ITextComponent> wrappedList = GuiUtilRenderComponents.splitText(chatMessage.getChatComponent(), chatWidth, this.mc.fontRendererObj, false, false);
				boolean isChatOpen = this.getChatOpen();
				for (ITextComponent chatLine : wrappedList) {
					if (isChatOpen && this.scrollPos > 0) {
						this.isScrolled = true;
						this.scroll(1);
					}
					String[] split = chatLine.getFormattedText().split("\u200b");
					TextRenderRegion[] regions = new TextRenderRegion[split.length];
					for(int i = 0; i < regions.length; i++) {
						regions[i] = new TextRenderRegion(split[i], chatMessage.getRGBColor(i));
					}
					this.wrappedChatLines.add(0, new WrappedChatLine(mc.ingameGUI.getUpdateCounter(), chatMessage, regions, alternateBackground));
				}
				while (this.wrappedChatLines.size() > 100) {
					this.wrappedChatLines.remove(this.wrappedChatLines.size() - 1);
				}
				alternateBackground = !alternateBackground;
				break;
			case Side:
				ChatTweaks.getSideChatHandler().addMessage(chatMessage);
				markAsRead(chatMessage);
				break;
			case Bottom:
				ChatTweaks.getBottomChatHandler().setMessage(chatMessage);
				markAsRead(chatMessage);
				break;
		}
	}

	@Override
	public void refreshChat() {
		for (IChatMessage chatMessage : activeChannel.getChatLines()) {
			addChatMessageForDisplay(chatMessage, activeChannel);
		}
	}

	@Override
	public void drawChat(int updateCounter) {
		if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
			float chatOpacity = this.mc.gameSettings.chatOpacity * 0.9f + 0.1f;
			int wrappedChatLinesCount = wrappedChatLines.size();
			if (wrappedChatLinesCount > 0) {
				boolean isChatOpen = this.getChatOpen();
				float chatScale = this.getChatScale();
				int chatWidth = MathHelper.ceiling_float_int((float) this.getChatWidth() / chatScale);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2f, 8f, 0f);
				GlStateManager.scale(chatScale, chatScale, 1f);

				int maxVisibleLines = this.getLineCount();
				int drawnLinesCount = 0;
				for (int lineIdx = 0; lineIdx + this.scrollPos < this.wrappedChatLines.size() && lineIdx < maxVisibleLines; lineIdx++) {
					WrappedChatLine chatLine = this.wrappedChatLines.get(lineIdx + this.scrollPos);
					int lifeTime = updateCounter - chatLine.timeCreated;
					if(lifeTime < 200 || isChatOpen) {
						int alpha = 255;
						if(!isChatOpen) {
							float percentage = (float) lifeTime / 200f;
							percentage = 1f - percentage;
							percentage = percentage * 10f;
							percentage = MathHelper.clamp_float(percentage, 0f, 1f);
							percentage = percentage * percentage;
							alpha = (int) (255f * percentage);
						}
						alpha = (int) ((float) alpha * chatOpacity);
						if(alpha > 3) {
							int x = 0;
							int y = -lineIdx * 9;
							if(chatLine.message.hasBackgroundColor()) {
								drawRect(-2, y - 9, chatWidth + 4, y, (chatLine.message.getBackgroundColor() & 0x00FFFFFF) + (alpha << 24));
							} else {
								drawRect(-2, y - 9, chatWidth + 4, y, ((chatLine.alternateBackground ? ChatTweaksConfig.backgroundColor1 : ChatTweaksConfig.backgroundColor2) & 0x00FFFFFF) + (alpha << 24));
							}
							GlStateManager.enableBlend();
							for(TextRenderRegion region : chatLine.regions) {
								x = fontRenderer.drawString(region.getText(), x, y - 8, (region.getColor() & 0x00FFFFFF) + (alpha << 24), true);
							}
							GlStateManager.disableAlpha();
							GlStateManager.disableBlend();
						}
						drawnLinesCount++;
					}
				}

				// Render the scroll bar, if necessary
				if(isChatOpen) {
					GlStateManager.translate(-3f, 0f, 0f);
					int fullHeight = wrappedChatLinesCount * fontRenderer.FONT_HEIGHT + wrappedChatLinesCount;
					int drawnHeight = drawnLinesCount * fontRenderer.FONT_HEIGHT + drawnLinesCount;
					int scrollY = this.scrollPos * drawnHeight / wrappedChatLinesCount;
					int scrollHeight = drawnHeight * drawnHeight / fullHeight;
					if (fullHeight != drawnHeight) {
						int alpha = scrollY > 0 ? 0xAA : 0x60;
						int color = this.isScrolled ? 0xCC3333 : 0x3333AA;
						drawRect(0, -scrollY, 2, -scrollY - scrollHeight, color + (alpha << 24));
						drawRect(2, -scrollY, 1, -scrollY - scrollHeight, 0xCCCCCC + (alpha << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	@SubscribeEvent
	public void onClientChat(ClientChatEvent event) {
		if (activeChannel.getOutgoingPrefix() != null) {
			event.setMessage(activeChannel.getOutgoingPrefix() + event.getMessage());
		}
	}

	@Override
	public void clearChatMessages() {
		for (ChatChannel channel : channels) {
			channel.clearChat();
		}
		unreadMessages.clear();
		chatLines.clear();
		chatLineCounter.set(START_ID);
	}

	public void checkHighlights(ChatMessage chatLine, @Nullable String sender, @Nullable String message) {
		if (sender == null && message != null) {
			DEFAULT_SENDER_MATCHER.reset(message);
			if (DEFAULT_SENDER_MATCHER.find()) {
				sender = DEFAULT_SENDER_MATCHER.group(1);
			}
		}
		if (sender != null) {
			sender = TextFormatting.getTextWithoutFormattingCodes(sender);
		}
		if (message == null) {
			message = chatLine.getChatComponent().getUnformattedText();
		}
		boolean isOwnMessage = false;
		if (sender != null) {
			EntityPlayer entityPlayer = Minecraft.getMinecraft().thePlayer;
			String playerName = entityPlayer != null ? entityPlayer.getDisplayNameString() : null;
			if (playerName != null && sender.equals(playerName)) {
				isOwnMessage = true;
			}
			if (!isOwnMessage) {
				if (ChatTweaksConfig.highlightName && playerName != null) {
					if (message.matches(".*(?:[\\p{Punct} ]|^)" + playerName + "(?:[\\p{Punct} ]|$).*")) {
						chatLine.setBackgroundColor(ChatTweaksConfig.backgroundColorHighlight);
					}
				}
				for (String highlight : ChatTweaksConfig.highlightStrings) {
					if (message.contains(highlight)) {
						chatLine.setBackgroundColor(ChatTweaksConfig.backgroundColorHighlight);
						break;
					}
				}
			}
		}
	}

	public void removeChatLine(ChatMessage chatLine) {
		removeChatLine(chatLine.getId());
	}

	public void removeChatLine(int id) {
		chatLines.remove(id);
		for (ChatChannel channel : channels) {
			channel.removeChatLine(id);
		}
	}

	public IChatMessage getChatLine(int id) {
		IChatMessage chatLine = activeChannel.getChatLine(id);
		if (chatLine != null) {
			return chatLine;
		}
		return chatLines.get(id);
	}

	public void setActiveChannel(ChatChannel channel) {
		ChatChannel oldActiveChannel = activeChannel;
		this.activeChannel = channel;
		ChatChannel displayChannel = (ChatChannel) channel.getDisplayChannel();
		if (displayChannel == null) {
			displayChannel = channel;
		}
		if (Minecraft.getMinecraft().ingameGUI != null && displayChannel.getMessageStyle() == MessageStyle.Chat) {
			for (IChatMessage chatLine : displayChannel.getChatLines()) {
				markAsRead(chatLine);
			}
			Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
		}
	}

	public void refreshChannel(ChatChannel channel) {
		channel.clearChat();
		for (ChatMessage chatLine : chatLines.values()) {
			if (!chatLine.isManaged() && channel.messageMatches(chatLine.getChatComponent().getUnformattedText()) && (!chatLine.isExclusiveChannel() || chatLine.getExclusiveChannel() == channel)) {
				channel.addChatLine(chatLine);
			}
		}
		channel.sortMessages();
	}

	public boolean isUnread(IChatMessage chatLine) {
		return unreadMessages.contains(chatLine.getId());
	}

	public void markAsRead(IChatMessage chatLine) {
		unreadMessages.remove(chatLine.getId());
	}

	public List<ChatChannel> getChannels() {
		return channels;
	}


	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	public IChatChannel getChannel(String name) {
		for (ChatChannel channel : channels) {
			if (channel.getName().equals(name)) {
				return channel;
			}
		}
		return null;
	}

	public void addChannel(ChatChannel channel) {
		channels.add(channel);
	}

	public boolean removeChannel(IChatChannel channel) {
		if (channels.size() == 1) {
			// Can't delete the last channel.
			return false;
		}
		//noinspection SuspiciousMethodCalls /// not suspicious at all
		int index = channels.indexOf(channel);
		if (index != -1) {
			if (channel == activeChannel) {
				IChatChannel nextChannel = getNextChatChannel(channel, true);
				if (nextChannel == null) {
					nextChannel = channels.get(0);
				}
				setActiveChannel((ChatChannel) nextChannel);
			}
			channels.remove(index);
		}
		return true;
	}

	public IChatChannel getNextChatChannel(IChatChannel currentChannel, boolean rollover) {
		int index = -1;
		if (currentChannel != null) {
			//noinspection SuspiciousMethodCalls /// THIS IS NOT SUSPICIOUS AT ALL INTELLIJ. CHATCHANNEL IMPLEMENTS ICHATCHANNEL. YOU SILLY
			index = channels.indexOf(currentChannel);
		}
		IChatChannel nextChannel = null;
		for (int i = index + 1; i < channels.size(); i++) {
			IChatChannel candidate = channels.get(i);
			if (candidate.getMessageStyle() == MessageStyle.Chat) {
				nextChannel = candidate;
				break;
			}
		}
		if (nextChannel == null && rollover) {
			for (int i = 0; i < index; i++) {
				IChatChannel candidate = channels.get(i);
				if (candidate.getMessageStyle() == MessageStyle.Chat) {
					nextChannel = candidate;
					break;
				}
			}
		}
		if (nextChannel == null) { // Everything is burning, retreat to just whatever channel is left
			nextChannel = channels.get(0);
		}
		return nextChannel;
	}

	public IChatChannel getPrevChatChannel(IChatChannel currentChannel, boolean rollover) {
		int index = -1;
		if (currentChannel != null) {
			//noinspection SuspiciousMethodCalls /// THIS IS NOT SUSPICIOUS AT ALL INTELLIJ. CHATCHANNEL IMPLEMENTS ICHATCHANNEL. YOU SILLY
			index = channels.indexOf(currentChannel);
		}
		IChatChannel nextChannel = null;
		for (int i = index - 1; i >= 0; i--) {
			IChatChannel candidate = channels.get(i);
			if (candidate.getMessageStyle() == MessageStyle.Chat) {
				nextChannel = candidate;
				break;
			}
		}
		if (nextChannel == null && (rollover || currentChannel == null)) {
			for (int i = channels.size() - 1; i > index; i--) {
				IChatChannel candidate = channels.get(i);
				if (candidate.getMessageStyle() == MessageStyle.Chat) {
					nextChannel = candidate;
					break;
				}
			}
		}
		if (nextChannel == null) { // Everything is burning, retreat to just whatever channel is left
			nextChannel = channels.get(0);
		}
		return nextChannel;
	}

}
