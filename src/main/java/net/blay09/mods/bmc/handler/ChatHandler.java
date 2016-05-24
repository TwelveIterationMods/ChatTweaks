package net.blay09.mods.bmc.handler;

import com.google.common.collect.*;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.IChatMessage;
import net.blay09.mods.bmc.api.MessageStyle;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.blay09.mods.bmc.api.IChatChannel;
import net.blay09.mods.bmc.api.event.*;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ChatHandler {

	private static final Matcher DEFAULT_SENDER_MATCHER = Pattern.compile("(<[^>]+>)").matcher("");
	private static final long MESSAGE_CLEANUP_TIME = 1000*60*10;

	private final AtomicInteger chatLineCounter = new AtomicInteger(10);
	private final Map<Integer, ChatMessage> chatLines = Maps.newHashMap();
	private final Set<Integer> unreadMessages = Sets.newHashSet();
	private final List<ChatChannel> channels = Lists.newArrayList();
	private final Multimap<ChatChannel, ChatMessage> tabMessages = ArrayListMultimap.create();

	private ChatChannel activeChannel;
	private long lastMessageCleanup;

	public ChatHandler() {
		lastMessageCleanup = System.currentTimeMillis();
	}

	private final List<ChatChannel> tmpMessageCandidates = Lists.newArrayList();
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPrintChatMessage(PrintChatMessageEvent event) {
		if (event.getChatLineId() != 0) {
			return;
		}

		ChatMessage chatLine = addChatLine(event.getMessage(), null, false);
		event.setChatLineId(chatLine.getId());

		if(chatLine.getTimestamp() - lastMessageCleanup >= MESSAGE_CLEANUP_TIME) {
			cleanOrphanMessages();
			lastMessageCleanup = chatLine.getTimestamp();
		}

		String unformattedText = event.getMessage().getUnformattedText();
		tmpMessageCandidates.clear();
		for (ChatChannel channel : channels) {
			if (channel.messageMatches(unformattedText)) {
				if(channel.isExclusive()) {
					tmpMessageCandidates.clear();
					tmpMessageCandidates.add(channel);
					chatLine.setExclusiveChannel(channel);
					break;
				}
				tmpMessageCandidates.add(channel);
			}
		}

		boolean isActiveChannelMessage = false;
		for(ChatChannel channel : tmpMessageCandidates) {
			ChatMessage newChatLine = channel.addChatLine(chatLine);
			if (channel == activeChannel) {
				markAsRead(chatLine);
				event.setMessage(newChatLine.getChatComponent());
			}
			switch(channel.getMessageStyle()) {
				case Chat:
					isActiveChannelMessage = true;
					break;
				case Side:
					BetterMinecraftChat.getSideChatHandler().addMessage(newChatLine);
					markAsRead(chatLine);
					break;
				case Bottom:
					BetterMinecraftChat.getBottomChatHandler().setMessage(newChatLine);
					markAsRead(chatLine);
					break;
			}
		}

		if (!isActiveChannelMessage) {
			event.setCanceled(true);
		}
	}

	private void cleanOrphanMessages() {
		Iterator<ChatMessage> it = chatLines.values().iterator();
		while(it.hasNext()) {
			boolean isOrphan = true;
			ChatMessage chatMessage = it.next();
			for(ChatChannel channel : channels) {
				if(channel.getChatLine(chatMessage.getId()) != null) {
					isOrphan = false;
					break;
				}
			}
			if(isOrphan) {
				it.remove();
			}
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (Keyboard.getEventKeyState() && Keyboard.isKeyDown(Keyboard.KEY_F3) && Keyboard.getEventKey() == Keyboard.KEY_D) {
			clearChat();
		}
	}

	@SubscribeEvent
	public void onClientChat(ClientChatEvent event) {
		if (activeChannel.getOutgoingPrefix() != null) {
			event.setMessage(activeChannel.getOutgoingPrefix() + event.getMessage());
		}
	}

	public void checkHighlights(ChatMessage chatLine, String sender, String message) {
		if(sender == null && message != null) {
			DEFAULT_SENDER_MATCHER.reset(message);
			if(DEFAULT_SENDER_MATCHER.find()) {
				sender = DEFAULT_SENDER_MATCHER.group(1);
			}
		}
		if(sender != null) {
			sender = TextFormatting.getTextWithoutFormattingCodes(sender);
		}
		if(message == null) {
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
				if (BetterMinecraftChatConfig.highlightName && playerName != null) {
					if (message.matches(".*(?:[\\p{Punct} ]|^)" + playerName + "(?:[\\p{Punct} ]|$).*")) {
						chatLine.setBackgroundColor(BetterMinecraftChatConfig.backgroundColorHighlight);
					}
				}
				for (String highlight : BetterMinecraftChatConfig.highlightStrings) {
					if (message.contains(highlight)) {
						chatLine.setBackgroundColor(BetterMinecraftChatConfig.backgroundColorHighlight);
						break;
					}
				}
			}
		}
	}

	public ChatMessage addChatLine(ITextComponent chatComponent, NBTTagCompound data, boolean printMessage) {
		int id = chatLineCounter.incrementAndGet();
		ChatMessage chatLine = new ChatMessage(id, chatComponent, data);
		if (printMessage) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponent, id);
		}
		chatLines.put(id, chatLine);
		unreadMessages.add(id);
		return chatLine;
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

	public void clearChat() {
		for (ChatChannel channel : channels) {
			channel.clearChat();
		}
		unreadMessages.clear();
		chatLines.clear();
		chatLineCounter.set(10);
	}

	public IChatMessage getChatLine(int id) {
		IChatMessage chatLine = activeChannel.getChatLine(id);
		if(chatLine != null) {
			return chatLine;
		}
		return chatLines.get(id);
	}

	public List<ChatChannel> getChannels() {
		return channels;
	}

	public void setActiveChannel(ChatChannel channel) {
		ChatChannel oldActiveChannel = activeChannel;
		this.activeChannel = channel;
		ChatChannel displayChannel = channel.getPassiveChannelName() != null ? (ChatChannel) getChannel(channel.getPassiveChannelName()) : channel;
		if(oldActiveChannel == displayChannel) {
			return;
		}
		if(Minecraft.getMinecraft().ingameGUI != null && displayChannel.getMessageStyle() == MessageStyle.Chat) {
			Minecraft.getMinecraft().ingameGUI.getChatGUI().chatLines.clear();
			for (IChatMessage chatLine : displayChannel.getChatLines()) {
				markAsRead(chatLine);
				Minecraft.getMinecraft().ingameGUI.getChatGUI().chatLines.add(0, new ChatLine(Minecraft.getMinecraft().ingameGUI.getUpdateCounter(), chatLine.getChatComponent(), chatLine.getId()));
			}
			Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
		}
	}

	public void refreshChannel(ChatChannel channel) {
		channel.clearChat();
		for(ChatMessage chatLine : chatLines.values()) {
			if (!chatLine.isManaged() && channel.messageMatches(chatLine.getChatComponent().getUnformattedText()) && (!chatLine.isExclusiveChannel() || chatLine.getExclusiveChannel() == channel)) {
				channel.addChatLine(chatLine);
			}
		}
		channel.sortMessages();
	}

	public ChatChannel getActiveChannel() {
		return activeChannel;
	}

	public boolean isUnread(IChatMessage chatLine) {
		return unreadMessages.contains(chatLine.getId());
	}

	public void markAsRead(IChatMessage chatLine) {
		unreadMessages.remove(chatLine.getId());
	}

	public IChatChannel getChannel(String name) {
		for(ChatChannel channel : channels) {
			if(channel.getName().equals(name)) {
				return channel;
			}
		}
		return null;
	}

	public void addChannel(ChatChannel channel) {
		channels.add(channel);
	}

	public boolean removeChannel(ChatChannel channel) {
		if(channels.size() == 1) {
			// Can't delete the last channel.
			return false;
		}
		int index = channels.indexOf(channel);
		if(index != -1) {
			if(channel == activeChannel) {
				ChatChannel nextChannel = getNextChatChannel(channel);
				if(nextChannel == null) {
					nextChannel = channels.get(0);
				}
				setActiveChannel(nextChannel);
			}
			channels.remove(index);
		}
		return true;
	}

	public ChatChannel getNextChatChannel(ChatChannel currentChannel) {
		int index = channels.size();
		if(currentChannel != null) {
			index = channels.indexOf(currentChannel);
		}
		ChatChannel nextChannel = null;
		for(int i = index - 1; i >= 0; i--) {
			ChatChannel candidate = channels.get(i);
			if(candidate.getMessageStyle() == MessageStyle.Chat) {
				nextChannel = candidate;
				break;
			}
		}
		if(nextChannel == null) {
			for(int i = index + 1; i < channels.size(); i++) {
				ChatChannel candidate = channels.get(i);
				if(candidate.getMessageStyle() == MessageStyle.Chat) {
					nextChannel = candidate;
					break;
				}
			}
		}
		return nextChannel;
	}
}
