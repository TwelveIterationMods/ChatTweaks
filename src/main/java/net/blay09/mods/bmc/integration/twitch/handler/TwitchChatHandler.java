package net.blay09.mods.bmc.integration.twitch.handler;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import net.blay09.javairc.IRCUser;
import net.blay09.javatmi.TMIAdapter;
import net.blay09.javatmi.TMIClient;
import net.blay09.javatmi.TwitchEmote;
import net.blay09.javatmi.TwitchUser;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.chat.IChatChannel;
import net.blay09.mods.bmc.api.chat.IChatMessage;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteScanner;
import net.blay09.mods.bmc.api.emote.PositionedEmote;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchAPI;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchGlobalEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchSubscriberEmotes;
import net.blay09.mods.bmc.image.ChatImageEmote;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegrationConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TwitchChatHandler extends TMIAdapter {

	private static class ChannelUser {
		private final String channel;
		private final String username;

		public ChannelUser(String channel, String username) {
			this.channel = channel;
			this.username = username;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ChannelUser that = (ChannelUser) o;
			return channel.equals(that.channel) && username.equals(that.username);
		}

		@Override
		public int hashCode() {
			int result = channel.hashCode();
			result = 31 * result + username.hashCode();
			return result;
		}
	}

	private static final Pattern PATTERN_ARGUMENT = Pattern.compile("%[ucmr]");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
	private final TwitchManager twitchManager;

	public TwitchChatHandler(TwitchManager twitchManager) {
		this.twitchManager = twitchManager;
	}

	private final Comparator<PositionedEmote> emoteComparator = new Comparator<PositionedEmote>() {
		@Override
		public int compare(PositionedEmote o1, PositionedEmote o2) {
			return o1.getStart() - o2.getStart();
		}
	};

	private final Predicate<IEmote> noTwitchEmotes = new Predicate<IEmote>() {
		@Override
		public boolean apply(@Nullable IEmote input) {
			return input != null && !(input.getLoader() instanceof TwitchGlobalEmotes || input.getLoader() instanceof TwitchSubscriberEmotes);
		}
	};

	private final IEmoteScanner emoteScanner = BetterMinecraftChatAPI.createEmoteScanner();
	private final Multimap<ChannelUser, IChatMessage> messages = ArrayListMultimap.create();
	private final Map<String, TwitchUser> users = Maps.newHashMap();
	private TwitchUser thisUser;

	private final List<IChatImage> tmpBadges = Lists.newArrayList();
	private final List<IChatImage> tmpEmotes = Lists.newArrayList();
	private final List<String> tmpBadgeNames = Lists.newArrayList();

	@Override
	public void onUserState(TMIClient client, String channel, TwitchUser user) {
		thisUser = user;
	}

	@Override
	public void onChatMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(client, twitchManager.isMultiMode() ? TwitchIntegrationConfig.multiMessageFormat : TwitchIntegrationConfig.singleMessageFormat, channel, user, message, false);
	}

	@Override
	public void onActionMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(client, twitchManager.isMultiMode() ? TwitchIntegrationConfig.multiActionFormat : TwitchIntegrationConfig.singleActionFormat, channel, user, message, true);
	}

	public void onTwitchChat(TMIClient client, String format, String channel, TwitchUser user, String message, boolean isAction) {
		TwitchChannel twitchChannel = twitchManager.getTwitchChannel(channel);
		if(twitchChannel != null && twitchChannel.isSubscribersOnly() && !user.isSubscriber() && !user.isMod()) {
			return;
		}

		boolean isSelf = user.getNick().equals(client.getIRCConnection().getNick());

		// Apply Twitch Emotes
		tmpEmotes.clear();
		List<PositionedEmote> emoteList = (isSelf && !user.hasEmotes()) ? emoteScanner.scanForEmotes(message, null) : emoteScanner.scanForEmotes(message, noTwitchEmotes);
		if(user.hasEmotes()) {
			for (TwitchEmote twitchEmote : user.getEmotes()) {
				IEmote emote = TwitchAPI.getEmoteById(twitchEmote.getId());
				if (emote != null) {
					emoteList.add(new PositionedEmote(emote, twitchEmote.getStart(), twitchEmote.getEnd()));
				}
			}
		}
		Collections.sort(emoteList, emoteComparator);

		// Apply Emotes
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for(PositionedEmote emoteData : emoteList) {
			if (index < emoteData.getStart()) {
				sb.append(message.substring(index, emoteData.getStart()));
			}
			int imageIndex = sb.length() + 1;
			for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
				sb.append(' ');
			}
			tmpEmotes.add(new ChatImageEmote(imageIndex, emoteData.getEmote()));
			index = emoteData.getEnd() + 1;
		}
		if(index < message.length()) {
			sb.append(message.substring(index));
		}
		message = sb.toString();

		// Apply Name Badges
		tmpBadges.clear();
		int badgeIndex = 0;
		if(isSelf && !user.hasBadges()) {
			tmpBadgeNames.clear();
			if(channel.equals("#" + user.getNick())) {
				tmpBadgeNames.add("broadcaster");
			} else if(user.isMod()) {
				tmpBadgeNames.add("moderator");
			}
			if(user.isTurbo()) {
				tmpBadgeNames.add("turbo");
			}
			if(user.isSubscriber()) {
				tmpBadgeNames.add("subscriber");
			}
			user.setBadges(tmpBadgeNames.toArray(new String[tmpBadgeNames.size()]));
		}
		if(user.hasBadges()) {
			for (String badgeName : user.getBadges()) {
				int slash = badgeName.indexOf('/');
				if (slash != -1) {
					badgeName = badgeName.substring(0, slash);
				}
				TwitchBadge badge;
				if (badgeName.equals("subscriber")) {
					badge = TwitchBadge.getSubscriberBadge(channel.substring(1));
				} else {
					badge = TwitchBadge.getBadge(badgeName);
				}
				if (badge != null) {
					IChatImage image = BetterMinecraftChatAPI.createImage(badgeIndex, badge.getChatRenderable(), badge.getTooltipProvider());
					badgeIndex += image.getSpaces();
					tmpBadges.add(image);
				}
			}
		}

		IChatChannel targetChannel = twitchChannel != null ? twitchChannel.getTargetTab() : null;

		// Format Message
		if(targetChannel != null && targetChannel.isShowTimestamp()) {
			format = TextFormatting.GRAY + dateFormat.format(new Date()) + " " + TextFormatting.RESET + format;
		}
		ITextComponent textComponent = formatComponent(format, channel, user, message, tmpBadges, tmpEmotes, null, isAction);
		IChatMessage chatMessage = BetterMinecraftChatAPI.addChatLine(textComponent, targetChannel);
		chatMessage.setManaged(true);
		for(IChatImage chatImage : tmpBadges) {
			chatMessage.addImage(chatImage);
		}
		for(IChatImage chatImage : tmpEmotes) {
			chatMessage.addImage(chatImage);
		}
		if(user.hasColor()) {
			int nameColor = BetterMinecraftChat.colorFromHex(user.getColor());
			chatMessage.addRGBColor(nameColor >> 16, nameColor >> 8 & 255, nameColor & 255);
		} else {
			chatMessage.addRGBColor(128, 128, 128);
		}
		if(isAction) {
			if(user.hasColor()) {
				int nameColor = BetterMinecraftChat.colorFromHex(user.getColor());
				chatMessage.addRGBColor(nameColor >> 16, nameColor >> 8 & 255, nameColor & 255);
			} else {
				chatMessage.addRGBColor(128, 128, 128);
			}
		}

		// Pipe message to tab
		if (targetChannel != null) {
			targetChannel.addManagedChatLine(chatMessage);
		}

		messages.put(new ChannelUser(channel, user.getNick().toLowerCase()), chatMessage);
		users.put(user.getNick().toLowerCase(), user);
	}

	@Override
	public void onSubscribe(TMIClient client, String channel, String username) {
		TwitchChannel twitchChannel = twitchManager.getTwitchChannel(channel);
		if(twitchManager.isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.subscribeMulti", channel, username), twitchChannel != null ? twitchChannel.getTargetTab() : null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.subscribe", username), twitchChannel != null ? twitchChannel.getTargetTab() : null);
		}
	}

	@Override
	public void onResubscribe(TMIClient client, String channel, String username, int months) {
		TwitchChannel twitchChannel = twitchManager.getTwitchChannel(channel);
		if(twitchManager.isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.resubscribeMulti", channel, username, months), twitchChannel != null ? twitchChannel.getTargetTab() : null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.resubscribeMulti", username, months), twitchChannel != null ? twitchChannel.getTargetTab() : null);
		}
	}

	@Override
	public void onWhisperMessage(TMIClient client, TwitchUser user, String message) {
		onWhisperMessage(client, user, getThisUser(client), message);
	}

	public void onWhisperMessage(TMIClient client, TwitchUser user, TwitchUser receiver, String message) {
		if(TwitchIntegrationConfig.showWhispers) {

			boolean isSelf = user.getNick().equals(client.getIRCConnection().getNick());

			// Apply Twitch Emotes
			tmpEmotes.clear();
			List<PositionedEmote> emoteList = (isSelf && !user.hasEmotes()) ? emoteScanner.scanForEmotes(message, null) : emoteScanner.scanForEmotes(message, noTwitchEmotes);
			if(user.hasEmotes()) {
				for (TwitchEmote twitchEmote : user.getEmotes()) {
					IEmote emote = TwitchAPI.getEmoteById(twitchEmote.getId());
					if (emote != null) {
						emoteList.add(new PositionedEmote(emote, twitchEmote.getStart(), twitchEmote.getEnd()));
					}
				}
			}
			Collections.sort(emoteList, emoteComparator);

			// Apply Emotes
			int index = 0;
			StringBuilder sb = new StringBuilder();
			for(PositionedEmote emoteData : emoteList) {
				if (index < emoteData.getStart()) {
					sb.append(message.substring(index, emoteData.getStart()));
				}
				int imageIndex = sb.length() + 1;
				for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
					sb.append(' ');
				}
				tmpEmotes.add(new ChatImageEmote(imageIndex, emoteData.getEmote()));
				index = emoteData.getEnd() + 1;
			}
			if(index < message.length()) {
				sb.append(message.substring(index));
			}
			message = sb.toString();

			IChatChannel targetTab = BetterMinecraftChatAPI.getChatChannel("(" + (isSelf ? receiver.getDisplayName() : user.getDisplayName()) + ")", true);

			// Format Message
			String format = TwitchIntegrationConfig.whisperMessageFormat;
			boolean isAction = message.startsWith("/me ") && message.length() > 4;
			if(isAction) {
				format = TwitchIntegrationConfig.whisperActionFormat;
				message = message.substring(4);
			}
			format = TextFormatting.GRAY + dateFormat.format(new Date()) + " " + TextFormatting.RESET + format;
			ITextComponent textComponent = formatComponent(format, null, user, message, null, tmpEmotes, receiver, isAction);
			IChatMessage chatMessage = BetterMinecraftChatAPI.addChatLine(textComponent, targetTab);
			chatMessage.setManaged(true);
			for(IChatImage chatImage : tmpEmotes) {
				chatMessage.addImage(chatImage);
			}
			if(user.hasColor()) {
				int nameColor = BetterMinecraftChat.colorFromHex(user.getColor());
				chatMessage.addRGBColor(nameColor >> 16, nameColor >> 8 & 255, nameColor & 255);
			} else {
				chatMessage.addRGBColor(128, 128, 128);
			}
			if(receiver.hasColor()) { // TODO this assumes that receiver is always in second place, which makes sense but isn't perfect
				int nameColor = BetterMinecraftChat.colorFromHex(receiver.getColor());
				chatMessage.addRGBColor(nameColor >> 16, nameColor >> 8 & 255, nameColor & 255);
			} else {
				chatMessage.addRGBColor(128, 128, 128);
			}
			if(isAction) { // TODO this assumes that message is always in third place, which makes sense but isn't perfect
				if(user.hasColor()) {
					int nameColor = BetterMinecraftChat.colorFromHex(user.getColor());
					chatMessage.addRGBColor(nameColor >> 16, nameColor >> 8 & 255, nameColor & 255);
				} else {
					chatMessage.addRGBColor(128, 128, 128);
				}
			}

			targetTab.setOutgoingPrefix("/twitch " + (isSelf ? receiver.getNick().toLowerCase() : user.getNick().toLowerCase()) + " ");
			targetTab.setTemporary(true);
			targetTab.addManagedChatLine(chatMessage);
			if(Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
				BetterMinecraftChat.getGuiChatHandler().updateChannelButtons(Minecraft.getMinecraft().currentScreen);
			}

			users.put(user.getNick().toLowerCase(), user);
		}
	}

	@Override
	public void onTimeout(TMIClient client, String channel, String username) {
		TwitchChannel twitchChannel = twitchManager.getTwitchChannel(channel);
		if(twitchChannel != null) {
			switch(twitchChannel.getDeletedMessages()) {
				case HIDE:
					for(IChatMessage message : messages.get(new ChannelUser(channel, username))) {
						BetterMinecraftChatAPI.removeChatLine(message.getId());
					}
					BetterMinecraftChatAPI.refreshChat();
					break;
				case STRIKETHROUGH:
					for(IChatMessage message : messages.get(new ChannelUser(channel, username))) {
						message.getChatComponent().getStyle().setStrikethrough(true);
					}
					BetterMinecraftChatAPI.refreshChat();
					break;
				case REPLACE:
					TwitchUser user = users.get(username);
					if(user == null) {
						user = new TwitchUser(new IRCUser(username, null, null));
					}
					for(IChatMessage message : messages.get(new ChannelUser(channel, username))) {
						ITextComponent removedComponent = formatComponent(twitchManager.isMultiMode() ? TwitchIntegrationConfig.multiMessageFormat : TwitchIntegrationConfig.singleMessageFormat, channel, user, TextFormatting.GRAY + "<message deleted>", null, null, null, false);
						message.setChatComponent(removedComponent);
						message.clearImages();
					}
					BetterMinecraftChatAPI.refreshChat();
					break;
			}
		}
	}

	@Override
	public void onClearChat(TMIClient client, String channel) {
		for(IChatMessage message : messages.values()) {
			BetterMinecraftChatAPI.removeChatLine(message.getId());
		}
		BetterMinecraftChatAPI.refreshChat();
	}

	@Override
	public void onUnhandledException(TMIClient client, Exception e) {
		e.printStackTrace();
		if(Minecraft.getMinecraft().thePlayer != null) {
			Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString(TextFormatting.RED + "Twitch Integration encountered an unhandled exception. The connection has been terminated. Please review your log files and let the mod developer know."));
		}
		twitchManager.disconnect();
	}

	public static ITextComponent formatComponent(String format, @Nullable String channel, TwitchUser user, String message, @Nullable List<IChatImage> nameBadges, @Nullable List<IChatImage> emotes, @Nullable TwitchUser whisperReceiver, boolean isAction) {
		String[] parts = format.split("(?<=" + PATTERN_ARGUMENT + ")|(?=" + PATTERN_ARGUMENT + ")"); // TODO cache this
		TextComponentString root = null;
		for(String key : parts) {
			if(key.charAt(0) == '%') {
				if(root == null) {
					root = new TextComponentString("");
				}
				switch(key.charAt(1)) {
					case 'c':
						root.appendText(channel != null ? channel : "%c");
						break;
					case 'u':
						int badgeOffset = 0;
						if(nameBadges != null) {
							for (IChatImage chatImage : nameBadges) {
								chatImage.setIndex(chatImage.getIndex() + root.getFormattedText().length());
								badgeOffset += chatImage.getSpaces();
							}
						}
						ITextComponent userComponent = new TextComponentString(StringUtils.repeat(' ', badgeOffset) + BetterMinecraftChatAPI.TEXT_FORMATTING_RGB + user.getDisplayName());
						root.appendSibling(userComponent);
						break;
					case 'r':
						if(whisperReceiver != null) {
							ITextComponent receiverComponent = new TextComponentString(BetterMinecraftChatAPI.TEXT_FORMATTING_RGB + whisperReceiver.getDisplayName());
							root.appendSibling(receiverComponent);
						} else {
							root.appendText("%r");
						}
						break;
					case 'm':
						if(emotes != null) {
							for (IChatImage chatImage : emotes) {
								chatImage.setIndex(chatImage.getIndex() + root.getFormattedText().length());
							}
						}
						root.appendText(isAction ? BetterMinecraftChatAPI.TEXT_FORMATTING_RGB + message : message);
						break;
				}
			} else {
				if(root == null) {
					root = new TextComponentString(key);
				} else {
					root.appendSibling(new TextComponentString(key));
				}
			}
		}
		if(root == null) {
			root = new TextComponentString(format);
		}
		return root;
	}

	public TwitchUser getThisUser(TMIClient client) {
		if(thisUser == null) {
			thisUser = new TwitchUser(new IRCUser(client.getIRCConnection().getNick(), null, null));
		}
		return thisUser;
	}

	public TwitchUser getUser(String username) {
		TwitchUser user = users.get(username.toLowerCase());
		if(user == null) {
			user = new TwitchUser(new IRCUser(username, null, null));
			users.put(username.toLowerCase(), user);
		}
		return user;
	}
}
