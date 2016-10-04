package net.blay09.mods.bmc.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.text.StyledString;
import net.blay09.mods.bmc.text.StyledStringSection;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatView {

	public static final Pattern defaultFilterPattern = Pattern.compile("(?:<(?<s>[^>]+)>)? ?(?<m>.*)"); // TODO this needs /me support
	public static final Pattern outputFormattingPattern = Pattern.compile("(\\\\~|~[0-9abcdefkolmnr])");
	private static final int MAX_MESSAGES = 100;

	private final String name;
	private final List<ChatChannel> channels = Lists.newArrayList();
	private String filterPattern;
	private String outputFormat = "$0";
	private MessageStyle messageStyle = MessageStyle.Chat;
	private String outgoingPrefix;
	private boolean isExclusive;
	private boolean isMuted;

	private Pattern compiledFilterPattern = defaultFilterPattern;
	private String compiledOutputFormat = outputFormat;
	private Matcher lastMatcher;
	private final List<ChatMessage> chatLines = Lists.newArrayList();

	public ChatView(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ChatView fromJson(JsonObject jsonView) {
		ChatView view = new ChatView(jsonView.get("name").getAsString());
		view.setFilterPattern(jsonView.get("filterPattern").getAsString());
		view.setOutputFormat(jsonView.get("outputFormat").getAsString());
		view.setMessageStyle(MessageStyle.valueOf(jsonView.get("style").getAsString()));
		view.setOutgoingPrefix(jsonView.has("outgoingPrefix") ? jsonView.get("outgoingPrefix").getAsString() : null);
		view.setExclusive(jsonView.get("isExclusive").getAsBoolean());
		view.setMuted(jsonView.get("isMuted").getAsBoolean());
		return view;
	}

	public JsonObject toJson() {
		JsonObject object = new JsonObject();
		object.addProperty("name", name);
		object.addProperty("filterPattern", filterPattern);
		object.addProperty("outputFormat", outputFormat);
		object.addProperty("style", messageStyle.name());
		object.addProperty("outgoingPrefix", outgoingPrefix);
		object.addProperty("isExclusive", isExclusive);
		object.addProperty("isMuted", isMuted);
		return object;
	}

	public boolean messageMatches(String message) {
		lastMatcher = compiledFilterPattern.matcher(message);
		return lastMatcher.matches();
	}

	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
		if(!filterPattern.isEmpty()) {
			try {
				compiledFilterPattern = Pattern.compile(filterPattern);
			} catch (PatternSyntaxException e) {
				compiledFilterPattern = defaultFilterPattern;
			}
		} else {
			compiledFilterPattern = defaultFilterPattern;
		}
	}

	public ChatMessage addChatLine(ChatMessage chatLine) {
		chatLine = chatLine.copy();
		chatLines.add(chatLine);
		if(chatLines.size() > MAX_MESSAGES) {
			chatLines.remove(0);
		}

		Pattern cacheMe = Pattern.compile("\\$(?:([0-9])|\\{([\\w])\\})");

		ITextComponent textComponent = chatLine.getChatComponent();
		if(!outputFormat.equals("$0")) {
			StyledString styledString = new StyledString(textComponent);
			Matcher matcher = cacheMe.matcher(outputFormat);
			StringBuffer sb = new StringBuffer();
			List<StyledStringSection> sections = Lists.newArrayList();
			int last = 0;
			while(matcher.find()) {
				int start;
				int end;
				String groupValue;
				String namedGroup = matcher.group(2);
				if(namedGroup != null) {
					start = lastMatcher.start(namedGroup);
					end = lastMatcher.end(namedGroup);
					groupValue = lastMatcher.group(namedGroup);
				} else {
					int group = Integer.parseInt(matcher.group(1));
					start = lastMatcher.start(group);
					end = lastMatcher.end(group);
					groupValue = lastMatcher.group(group);
				}
				int dstStart = sb.length() + matcher.start() - last;
				int dstEnd = dstStart + groupValue.length();
				last = matcher.end();
				matcher.appendReplacement(sb, groupValue);
				sections.addAll(styledString.getStyleSections(start, end, dstStart, dstEnd));
			}
			matcher.appendTail(sb);
			StyledString output = new StyledString(sb.toString(), sections);
			textComponent = output.toTextComponent();
		}
		chatLine.setTextComponent(textComponent);
		return chatLine;
	}

	@Nullable
	private String tryGetGroup(Matcher matcher, String name, @Nullable String defaultVal) {
		try {
			return matcher.group(name);
		} catch (IllegalArgumentException e) {
			return defaultVal;
		}
	}

	public boolean hasUnreadMessages() {
		return false; // TODO implement me
	}

	public void markAsUnread(ChatMessage message) {
		// TODO implement me
	}

	public void addChannel(ChatChannel channel) {
		channels.add(channel);
	}

	public Collection<ChatChannel> getChannels() {
		return channels;
	}

	public boolean isExclusive() {
		return isExclusive;
	}

	public void setExclusive(boolean exclusive) {
		isExclusive = exclusive;
	}

	public MessageStyle getMessageStyle() {
		return messageStyle;
	}

	public void setMessageStyle(MessageStyle messageStyle) {
		this.messageStyle = messageStyle;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMuted(boolean isMuted) {
		this.isMuted = isMuted;
	}

	public String getOutgoingPrefix() {
		return outgoingPrefix;
	}

	public void setOutgoingPrefix(@Nullable String outgoingPrefix) {
		this.outgoingPrefix = outgoingPrefix;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
}
