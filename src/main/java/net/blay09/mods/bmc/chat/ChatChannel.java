package net.blay09.mods.bmc.chat;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksConfig;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.chat.IChatChannel;
import net.blay09.mods.bmc.api.chat.IChatMessage;
import net.blay09.mods.bmc.api.chat.MessageStyle;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.chat.badges.PatronBadges;
import net.blay09.mods.bmc.balyware.textcomponent.MultiTextComponentTransformer;
import net.blay09.mods.bmc.balyware.textcomponent.TextComponentTransformer;
import net.blay09.mods.bmc.chat.emotes.EmoteTransformer;
import net.blay09.mods.bmc.chat.badges.NameTransformer;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntry;
import net.blay09.mods.bmc.balyware.textcomponent.StringRegion;
import net.blay09.mods.bmc.balyware.textcomponent.StringWithMeta;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatChannel implements IChatChannel {

	public static final Pattern DEFAULT_PATTERN = Pattern.compile("(?:<(?<s>[^>]+)>)?(?<m>.*)");

	public static final Matcher MATCHER_NAMED_GROUPS = Pattern.compile("\\$\\{(\\w+)\\}").matcher("");
	public static final Matcher MATCHER_GROUPS = Pattern.compile("\\$([0-9])").matcher("");
	private static final int MAX_MESSAGES = 100;

	private final Map<Integer, IChatMessage> chatLineMap = Maps.newHashMap();
	private final List<IChatMessage> chatLines = Lists.newArrayList();
	private final List<IChatMessage> managedChatLines = Lists.newArrayList();
	private final MultiTextComponentTransformer transformers = new MultiTextComponentTransformer();

	private String pattern;
	private String name;
	private String format = "$0";
	private String formattedFormat = format;
	private String outgoingPrefix;
	private boolean isMuted;
	private boolean isExclusive;
	private boolean isTemporary;
	private boolean showTimestamps;
	private MessageStyle messageStyle = MessageStyle.Chat;
	private String passiveChannelName;

	private Matcher messageMatcher = DEFAULT_PATTERN.matcher("");

	private final EmoteTransformer emoteTransformer = new EmoteTransformer() {
		@Override
		public void finish(ITextComponent chatComponent, ITextComponent transformedComponent) {
			for(IChatImage image : getImages()) {
				lastAdded.addImage(image);
			}
		}
	};

	private final NameTransformer nameTransformer = new NameTransformer() {
		@Override
		public void begin(ITextComponent chatComponent) {
			super.begin(chatComponent);
			senderName = tryGroup(messageMatcher, "s", null);
			if(senderName != null) {
				if(ChatTweaksConfig.enableNameBadges) {
					nameBadge = PatronBadges.getBadgeForPlayer(senderName);
				}
				if(ChatTweaksConfig.randomNameColors) {
					nameColor = RandomNameColors.getRandomNameColor(senderName);
				}
			}
		}

		@Override
		public void finish(ITextComponent chatComponent, ITextComponent transformedComponent) {
			if(getImage() != null) {
				lastAdded.addImage(getImage());
			}
		}
	};

	private final TextComponentTransformer timestampTransformer = new TextComponentTransformer() {
		private final SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		private boolean isFirst;
		@Override
		public void begin(ITextComponent chatComponent) {
			isFirst = true;
		}

		@Override
		public String transformText(ITextComponent component, String text) {
			if(isFirst) {
				isFirst = false;
				return TextFormatting.GRAY + dateFormat.format(new Date(lastAdded.getTimestamp())) + " " + TextFormatting.RESET + text;
			}
			return text;
		}
	};

	private String lastMatched;
	private ChatMessage lastAdded;

	public ChatChannel(String name) {
		this(name, null);
	}

	public ChatChannel(String name, String pattern) {
		this.name = name;
		setFilterPattern(pattern);
		transformers.addTransformer(nameTransformer);
		transformers.addTransformer(emoteTransformer);
	}

	public static ChatChannel fromJson(JsonObject object) {
		ChatChannel channel = new ChatChannel(object.get("name").getAsString(), object.get("pattern").getAsString());
		channel.setExclusive(object.get("exclusive").getAsBoolean());
		channel.setMessageStyle(MessageStyle.valueOf(object.get("style").getAsString()));
		channel.setFormat(object.get("format").getAsString());
		channel.setMuted(object.get("muted").getAsBoolean());
		channel.setOutgoingPrefix(object.has("prefix") ? object.get("prefix").getAsString() : null);
		channel.setShowTimestamps(object.get("timestamps").getAsBoolean());
		return channel;
	}

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		object.addProperty("pattern", pattern);
		object.addProperty("exclusive", isExclusive);
		object.addProperty("style", messageStyle.name());
		object.addProperty("format", format);
		object.addProperty("muted", isMuted);
		object.addProperty("prefix", outgoingPrefix);
		object.addProperty("timestamps", showTimestamps);
		return object;
	}

	public boolean messageMatches(String message) {
		lastMatched = message;
		messageMatcher.reset(message);
		return messageMatcher.matches();
	}

	public ChatMessage addChatLine(ChatMessage chatLine) {
		assert Objects.equals(lastMatched, chatLine.getChatComponent().getUnformattedText());

		chatLine = chatLine.copy();
		if(chatLine.getImages() != null) {
			chatLine.getImages().clear();
		}
		lastAdded = chatLine;

		String sender = tryGroup(messageMatcher, "s", null);
		String message = tryGroup(messageMatcher, "m", null);
		ChatTweaks.getChatHandler().checkHighlights(chatLine, sender, message);

		ITextComponent textComponent = chatLine.getChatComponent();
		if(!format.equals("$0")) {
			if (sender != null && message != null) {
				textComponent = formatComponent(StringWithMeta.fromTextComponent(textComponent), sender, message);
			} else if(messageStyle != MessageStyle.Chat) {
				textComponent = formatComponentSimple(textComponent);
			}
		}
		if(transformers.getTransformerCount() > 0) {
			textComponent = transformers.walkTextComponent(textComponent);
		}
		chatLine.setChatComponent(textComponent);

		chatLineMap.put(chatLine.getId(), chatLine);
		chatLines.add(chatLine);
		if(chatLines.size() > MAX_MESSAGES) {
			removeChatLine(chatLines.get(0).getId());
		}
		return chatLine;
	}

	private ITextComponent formatComponent(StringWithMeta metaText, String sender, String message) {
		int origSenderIndex = metaText.getText().indexOf(sender);
		int origMessageIndex = metaText.getText().indexOf(message);
		List<MetaEntry> senderMeta = metaText.getMetaForRange(origSenderIndex, sender.length());
		List<MetaEntry> messageMeta = metaText.getMetaForRange(origMessageIndex, message.length());
		MATCHER_NAMED_GROUPS.reset(formattedFormat);
		StringBuffer sb = new StringBuffer();
		while (MATCHER_NAMED_GROUPS.find()) {
			String group = MATCHER_NAMED_GROUPS.group(1);
			MATCHER_NAMED_GROUPS.appendReplacement(sb, tryGroup(messageMatcher, group, "\\${" + group + "}").trim());
		}
		MATCHER_NAMED_GROUPS.appendTail(sb);
		MATCHER_GROUPS.reset(sb.toString());
		sb = new StringBuffer();
		while (MATCHER_GROUPS.find()) {
			int group = Integer.parseInt(MATCHER_GROUPS.group(1));
			MATCHER_GROUPS.appendReplacement(sb, group >= 0 && group < messageMatcher.groupCount() ? messageMatcher.group(group).trim() : "\\$" + group);
		}
		MATCHER_GROUPS.appendTail(sb);
		String outputMessage = sb.toString();
		// This could break if someone switches sender and message around and the message contains the sender...
		// ...but who would do that? Just fix it when someone complains.
		int newSenderIndex = outputMessage.indexOf(sender);
		int newMessageIndex = outputMessage.indexOf(message);
		int senderOffset = newSenderIndex - origSenderIndex;
		int messageOffset = newMessageIndex - origMessageIndex;
		TreeMultimap<StringRegion, MetaEntry> metadata = TreeMultimap.create();
		for (MetaEntry entry : senderMeta) {
			metadata.put(new StringRegion(entry.getIndex() + senderOffset, entry.getLength()), entry.copy(entry.getIndex() + senderOffset));
		}
		for (MetaEntry entry : messageMeta) {
			metadata.put(new StringRegion(entry.getIndex() + messageOffset, entry.getLength()), entry.copy(entry.getIndex() + messageOffset));
		}
		StringWithMeta outputMetaText = new StringWithMeta(outputMessage, metadata);
		return outputMetaText.toChatComponent();
	}

	private ITextComponent formatComponentSimple(ITextComponent component) {
		MATCHER_NAMED_GROUPS.reset(formattedFormat);
		StringBuffer sb = new StringBuffer();
		while (MATCHER_NAMED_GROUPS.find()) {
			String group = MATCHER_NAMED_GROUPS.group(1);
			MATCHER_NAMED_GROUPS.appendReplacement(sb, tryGroup(messageMatcher, group, "\\${" + group + "}").trim());
		}
		MATCHER_NAMED_GROUPS.appendTail(sb);
		MATCHER_GROUPS.reset(sb.toString());
		sb = new StringBuffer();
		while (MATCHER_GROUPS.find()) {
			int group = Integer.parseInt(MATCHER_GROUPS.group(1));
			if(group == 0) {
				MATCHER_GROUPS.appendReplacement(sb, component.getFormattedText());
			} else {
				MATCHER_GROUPS.appendReplacement(sb, group >= 0 && group < messageMatcher.groupCount() ? messageMatcher.group(group).trim() : "\\$" + group);
			}
		}
		MATCHER_GROUPS.appendTail(sb);
		String outputMessage = sb.toString();
		return new TextComponentString(outputMessage);
	}

	@Nullable
	private String tryGroup(Matcher matcher, String name, @Nullable String defaultVal) {
		try {
			return matcher.group(name);
		} catch (IllegalArgumentException e) {
			return defaultVal;
		}
	}

	@Override
	public IChatMessage getChatLine(int id) {
		return chatLineMap.get(id);
	}

	public Collection<IChatMessage> getChatLines() {
		return chatLines;
	}

	@Override
	public void addManagedChatLine(IChatMessage chatLine) {
		chatLines.add(chatLine);
		managedChatLines.add(chatLine);
		chatLineMap.put(chatLine.getId(), chatLine);
		if(ChatTweaks.getChatHandler().getActiveChannel() == this) {
			ChatTweaks.getChatHandler().markAsRead(chatLine);
		}
		if(chatLines.size() > MAX_MESSAGES) {
			removeChatLine(chatLines.get(0).getId());
		}
	}

	public void removeChatLine(int id) {
		IChatMessage chatLine = chatLineMap.remove(id);
		chatLines.remove(chatLine);
		managedChatLines.remove(chatLine);
	}

	@Override
	public void clearChat() {
		chatLineMap.clear();
		chatLines.clear();
		chatLines.addAll(managedChatLines);
	}

	@Override
	public boolean hasUnreadMessages() {
		for(IChatMessage chatLine : chatLines) {
			if(ChatTweaks.getChatHandler().isUnread(chatLine)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void disableDefaultNameTransformer() {
		transformers.removeTransformer(nameTransformer);
	}

	@Override
	public void setFilterPattern(String pattern) {
		this.pattern = pattern != null ? pattern : DEFAULT_PATTERN.pattern();
		try {
			this.messageMatcher.usePattern(Pattern.compile(this.pattern));
		} catch (PatternSyntaxException e) {
			this.messageMatcher.usePattern(DEFAULT_PATTERN);
		}
	}

	@Override
	public String getFilterPattern() {
		return pattern;
	}

	public void setShowTimestamps(boolean showTimestamps) {
		this.showTimestamps = showTimestamps;
		if(showTimestamps) {
			if(!transformers.contains(timestampTransformer)) {
				transformers.insertBefore(timestampTransformer, emoteTransformer);
			}
		} else {
			transformers.removeTransformer(timestampTransformer);
		}
	}

	@Override
	public boolean isShowTimestamp() {
		return showTimestamps;
	}

	@Override
	public void setFormat(String format) {
		this.format = Strings.isNullOrEmpty(format) ? "$0" : format;
		Matcher matcher = Pattern.compile("(\\\\~|~[0-9abcdefkolmnr])").matcher(this.format);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(sb, group.equals("\\~") ? "~" : "\u00a7" + group.substring(1));
		}
		matcher.appendTail(sb);
		this.formattedFormat = sb.toString();
	}

	public String getFormat() {
		return format;
	}

	@Override
	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	@Override
	public boolean isMuted() {
		return isMuted;
	}

	@Override
	public boolean isHidden() {
		return messageStyle != MessageStyle.Chat;
	}

	@Override
	public boolean isExclusive() {
		return isExclusive;
	}

	@Override
	public void setExclusive(boolean exclusive) {
		isExclusive = exclusive;
	}

	@Override
	public MessageStyle getMessageStyle() {
		return messageStyle;
	}

	@Override
	public void setMessageStyle(MessageStyle messageStyle) {
		this.messageStyle = messageStyle;
	}

	@Override
	public void setOutgoingPrefix(String outgoingPrefix) {
		this.outgoingPrefix = outgoingPrefix;
	}

	@Override
	public String getOutgoingPrefix() {
		return outgoingPrefix;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Nullable
	public IChatChannel getDisplayChannel() {
		return passiveChannelName != null ? BetterMinecraftChatAPI.getChatChannel(passiveChannelName, false) : null;
	}

	@Override
	public void setDisplayChannel(@Nullable IChatChannel passiveChannel) {
		this.passiveChannelName = passiveChannel != null ? passiveChannel.getName() : null;
	}

	@Override
	public boolean isTemporary() {
		return isTemporary;
	}

	@Override
	public void setTemporary(boolean temporary) {
		isTemporary = temporary;
	}

	public void sortMessages() {
		Collections.sort(chatLines, new Comparator<IChatMessage>() {
			@Override
			public int compare(IChatMessage o1, IChatMessage o2) {
				return o1.getId() - o2.getId();
			}
		});
	}
}
