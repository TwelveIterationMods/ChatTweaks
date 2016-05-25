package net.blay09.mods.bmc.integration.twitch;

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
import net.blay09.mods.bmc.api.IChatChannel;
import net.blay09.mods.bmc.api.IChatMessage;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.chat.emotes.EmoteScanner;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchAPI;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchGlobalEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchSubscriberEmotes;
import net.blay09.mods.bmc.image.ChatImageEmote;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TwitchChatHandler extends TMIAdapter {

	private static final Comparator<EmoteScanner.PositionedEmote> emoteComparator = new Comparator<EmoteScanner.PositionedEmote>() {
		@Override
		public int compare(EmoteScanner.PositionedEmote o1, EmoteScanner.PositionedEmote o2) {
			return o1.getStart() - o2.getStart();
		}
	};

	private static final Predicate<IEmote> noTwitchEmotes = new Predicate<IEmote>() {
		@Override
		public boolean apply(@Nullable IEmote input) {
			return input != null && !(input.getLoader() instanceof TwitchGlobalEmotes || input.getLoader() instanceof TwitchSubscriberEmotes);
		}
	};

	private static final Pattern PATTERN_ARGUMENT = Pattern.compile("%[ucmr]");

	private static final EmoteScanner emoteScanner = new EmoteScanner();
	private static final Multimap<String, IChatMessage> messages = ArrayListMultimap.create();
	private static final Map<String, TwitchUser> users = Maps.newHashMap();

	private static final List<IChatImage> tmpBadges = Lists.newArrayList();
	private static final List<IChatImage> tmpEmotes = Lists.newArrayList();


	@Override
	public void onChatMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(TwitchIntegration.isMultiMode() ? TwitchIntegration.multiMessageFormat : TwitchIntegration.singleMessageFormat, channel, user, message, false);
	}

	@Override
	public void onActionMessage(TMIClient client, String channel, TwitchUser user, String message) {
		onTwitchChat(TwitchIntegration.isMultiMode() ? TwitchIntegration.multiActionFormat : TwitchIntegration.singleActionFormat, channel, user, message, true);
	}

	public void onTwitchChat(String format, String channel, TwitchUser user, String message, boolean isAction) {
		TwitchChannel twitchChannel = TwitchIntegration.getTwitchChannel(channel);
		if(twitchChannel != null && twitchChannel.isSubscribersOnly() && !user.isSubscriber() && !user.isMod()) {
			return;
		}

		// Apply Twitch Emotes
		tmpEmotes.clear();
		List<EmoteScanner.PositionedEmote> emoteList = emoteScanner.scanForEmotes(message, noTwitchEmotes);
		for(TwitchEmote twitchEmote : user.getEmotes()) {
			IEmote emote = TwitchAPI.getEmoteById(twitchEmote.getId());
			if (emote != null) {
				emoteList.add(new EmoteScanner.PositionedEmote(emote, twitchEmote.getStart(), twitchEmote.getEnd()));
			}
		}
		Collections.sort(emoteList, emoteComparator);

		// Apply Emotes
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for(EmoteScanner.PositionedEmote emoteData : emoteList) {
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

		IChatChannel targetChannel = twitchChannel != null ? twitchChannel.getTargetChannel() : null;

		// Format Message
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

		messages.put(user.getNick(), chatMessage);
		users.put(user.getNick(), user);
	}

	@Override
	public void onSubscribe(TMIClient client, String channel, String username) {
		TwitchChannel twitchChannel = TwitchIntegration.getTwitchChannel(channel);
		if(TwitchIntegration.isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.subscribeMulti", channel, username), twitchChannel != null ? twitchChannel.getTargetChannel() : null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.subscribe", username), twitchChannel != null ? twitchChannel.getTargetChannel() : null);
		}
	}

	@Override
	public void onResubscribe(TMIClient client, String channel, String username, int months) {
		TwitchChannel twitchChannel = TwitchIntegration.getTwitchChannel(channel);
		if(TwitchIntegration.isMultiMode()) {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.resubscribeMulti", channel, username, months), twitchChannel != null ? twitchChannel.getTargetChannel() : null);
		} else {
			BetterMinecraftChatAPI.addChatLine(new TextComponentTranslation(TwitchIntegration.MOD_ID + ":chat.resubscribeMulti", username, months), twitchChannel != null ? twitchChannel.getTargetChannel() : null);
		}
	}

	@Override
	public void onWhisperMessage(TMIClient client, TwitchUser user, String message) {
		if(TwitchIntegration.showWhispers) {
			// Apply Twitch Emotes
			tmpEmotes.clear();
			List<EmoteScanner.PositionedEmote> emoteList = emoteScanner.scanForEmotes(message, noTwitchEmotes);
			for(TwitchEmote twitchEmote : user.getEmotes()) {
				IEmote emote = TwitchAPI.getEmoteById(twitchEmote.getId());
				if (emote != null) {
					emoteList.add(new EmoteScanner.PositionedEmote(emote, twitchEmote.getStart(), twitchEmote.getEnd()));
				}
			}
			Collections.sort(emoteList, emoteComparator);

			// Apply Emotes
			int index = 0;
			StringBuilder sb = new StringBuilder();
			for(EmoteScanner.PositionedEmote emoteData : emoteList) {
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

			// Format Message
			boolean isAction = message.startsWith("/me ") && message.length() > 4;
			if(isAction) {
				message = message.substring(4);
			}
			TwitchUser thisUser = users.get(client.getIRCConnection().getNick());
			if(thisUser == null) {
				thisUser = new TwitchUser(new IRCUser(client.getIRCConnection().getNick(), null, null));
			}
			ITextComponent textComponent = formatComponent(TwitchIntegration.whisperMessageFormat, null, user, message, null, tmpEmotes, thisUser, isAction);
			IChatMessage chatMessage = BetterMinecraftChatAPI.addChatLine(textComponent, null);
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
			if(thisUser.hasColor()) { // TODO this assumes that receiver is always in second place, which makes sense but isn't perfect
				int nameColor = BetterMinecraftChat.colorFromHex(thisUser.getColor());
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

			messages.put(user.getNick(), chatMessage);
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
					TwitchUser user = users.get(username);
					if(user == null) {
						user = new TwitchUser(new IRCUser(username, null, null));
					}
					for(IChatMessage message : messages.get(username)) {
						ITextComponent removedComponent = formatComponent(TwitchIntegration.isMultiMode() ? TwitchIntegration.multiMessageFormat : TwitchIntegration.singleMessageFormat, channel, user, TextFormatting.GRAY + "<message deleted>", null, null, null, false);
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

	public ITextComponent formatComponent(String format, @Nullable String channel, TwitchUser user, String message, @Nullable List<IChatImage> nameBadges, @Nullable List<IChatImage> emotes, @Nullable TwitchUser whisperReceiver, boolean isAction) {
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
}
