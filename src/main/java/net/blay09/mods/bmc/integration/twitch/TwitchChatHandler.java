package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.blay09.javatmi.TMIAdapter;
import net.blay09.javatmi.TMIClient;
import net.blay09.javatmi.TwitchEmote;
import net.blay09.javatmi.TwitchUser;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IChatChannel;
import net.blay09.mods.bmc.api.IChatMessage;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.chat.emotes.EmoteScanner;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchAPI;
import net.blay09.mods.bmc.image.ChatImageEmote;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class TwitchChatHandler extends TMIAdapter {

	private static final Pattern PATTERN_ARGUMENT = Pattern.compile("%[ucm]");

	private static final EmoteScanner emoteScanner = new EmoteScanner();
	private static final List<TwitchChannel> activeChannels = Lists.newArrayList();
	private static final Multimap<String, IChatMessage> messages = ArrayListMultimap.create();

	private static final List<IChatImage> tmpBadges = Lists.newArrayList();
	private static final List<IChatImage> tmpEmotes = Lists.newArrayList();

	private boolean isMultiMode() {
		return activeChannels.size() > 1;
	}

	@Override
	public void onChatMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(isMultiMode() ? TwitchIntegration.multiMessageFormat : TwitchIntegration.singleMessageFormat, channel, user, message);
	}

	@Override
	public void onActionMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(isMultiMode() ? TwitchIntegration.multiEmoteFormat : TwitchIntegration.singleEmoteFormat, channel, user, message);
	}

	public void onTwitchChat(String format, String channel, TwitchUser user, String message) {
		TwitchChannel twitchChannel = TwitchIntegration.getTwitchChannel(channel);
		if(twitchChannel != null && twitchChannel.isSubscribersOnly() && !user.isSubscriber()) {
			return;
		}
		// Apply Twitch Emotes
		tmpEmotes.clear();
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for(TwitchEmote emoteData : user.getEmotes()) {
			IEmote emote = TwitchAPI.getEmoteById(emoteData.getId());
			if (emote == null) {
				continue;
			}
			if (index < emoteData.getStart()) {
				sb.append(message.substring(index, emoteData.getStart()));
			}
			int imageIndex = sb.length() + 1;
			for (int i = 0; i < emote.getWidthInSpaces(); i++) {
				sb.append(' ');
			}
			tmpEmotes.add(new ChatImageEmote(imageIndex, emote));
			index = emoteData.getEnd() + 1;
		}
		if(index < message.length()) {
			sb.append(message.substring(index));
		}
		message = sb.toString();

		// Apply Custom Emotes
//		index = 0;
//		sb = new StringBuilder();
//		for(EmoteScanner.CustomEmote emoteData : emoteScanner.scanForEmotes(message)) {
//			if (index < emoteData.getStart()) {
//				sb.append(message.substring(index, emoteData.getStart()));
//			}
//			int imageIndex = sb.length() + 1;
//			for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
//				sb.append(' ');
//			}
//			tmpEmotes.add(new ChatImageEmote(imageIndex, emoteData.getEmote()));
//			index = emoteData.getEnd() + 1;
//		}
//		if(index < message.length()) {
//			sb.append(message.substring(index));
//		}
//		message = sb.toString();

		// Apply Name Badges
		tmpBadges.clear();
		int badgeIndex = 0;
		for(String badgeName : user.getBadges()) {
			int slash = badgeName.indexOf('/');
			if(slash != -1) {
				badgeName = badgeName.substring(0, slash);
			}
			TwitchBadge badge;
			if(badgeName.equals("subscriber")) {
				badge = TwitchBadge.getSubscriberBadge(channel.substring(1));
			} else {
				badge = TwitchBadge.getBadge(badgeName);
			}
			if(badge != null) {
				IChatImage image = BetterMinecraftChatAPI.createImage(badgeIndex, badge.getChatRenderable(), badge.getTooltipProvider());
				badgeIndex += image.getSpaces();
				tmpBadges.add(image);
			}
		}

		// Format Message
		ITextComponent textComponent = formatComponent(format, channel, user, message, badgeIndex);
		IChatMessage chatMessage = BetterMinecraftChatAPI.addChatLine(textComponent, null);
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

		// Pipe message to tab
		if(twitchChannel != null) {
			IChatChannel targetChannel = twitchChannel.getTargetChannel();
			if (targetChannel != null) {
				targetChannel.addManagedChatLine(chatMessage);
			}
		}

		messages.put(user.getNick(), chatMessage);
	}

	@Override
	public void onSubscribe(TMIClient client, String channel, String username) {
		if(isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentString("[" + channel + "] " + username + " has just subscribed!"), null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentString(username + " has just subscribed!"), null);
		}
	}

	@Override
	public void onResubscribe(TMIClient client, String channel, String username, int months) {
		if(isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentString("[" + channel + "] " + username + " has subscribed for " + months + " in a row!"), null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentString(username + " has subscribed for " + months + " in a row!"), null);
		}
	}

	@Override
	public void onWhisperMessage(TMIClient client, TwitchUser user, String message) {
		if(TwitchIntegration.showWhispers) {

		}
	}

	@Override
	public void onTimeout(TMIClient client, String channel, String username) {
		TwitchChannel twitchChannel = TwitchIntegration.getTwitchChannel(channel);
		if(twitchChannel != null) {
			switch(twitchChannel.getDeletedMessages()) {
				case HIDE:
					for(IChatMessage message : messages.get(username)) {
						BetterMinecraftChatAPI.removeChatLine(message.getId());
					}
					BetterMinecraftChatAPI.refreshChat();
					break;
				case STRIKETHROUGH:
					for(IChatMessage message : messages.get(username)) {
						message.getChatComponent().getStyle().setStrikethrough(true);
					}
					BetterMinecraftChatAPI.refreshChat();
					break;
				case REPLACE:
					for(IChatMessage message : messages.get(username)) {
						ITextComponent removedComponent = new TextComponentString(username + ": <message deleted>");
						removedComponent.getStyle().setItalic(true);
						removedComponent.getStyle().setColor(TextFormatting.GRAY);
						message.setChatComponent(removedComponent);
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

	public ITextComponent formatComponent(String format, String channel, TwitchUser user, String message, int badgeOffset) {
		String[] parts = format.split("(?<=" + PATTERN_ARGUMENT + ")|(?=" + PATTERN_ARGUMENT + ")"); // TODO cache this
		TextComponentString root = null;
		for(String key : parts) {
			if(key.charAt(0) == '%') {
				if(root == null) {
					root = new TextComponentString("");
				}
				switch(key.charAt(1)) {
					case 'c':
						root.appendText(channel);
						break;
					case 'u':
						for(IChatImage chatImage : tmpBadges) {
							chatImage.setIndex(chatImage.getIndex() + root.getFormattedText().length());
						}
						ITextComponent userComponent = new TextComponentString(StringUtils.repeat(' ', badgeOffset) + BetterMinecraftChatAPI.TEXT_FORMATTING_RGB + user.getDisplayName());
						root.appendSibling(userComponent);
						break;
					case 'm':
						for(IChatImage chatImage : tmpEmotes) {
							chatImage.setIndex(chatImage.getIndex() + root.getFormattedText().length());
						}
						root.appendText(message);
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
		return root;
	}
}
