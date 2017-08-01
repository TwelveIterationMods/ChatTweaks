package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.chattweaks.ChatManager;
import net.blay09.mods.chattweaks.chat.emotes.EmoteScanner;
import net.blay09.mods.chattweaks.chat.emotes.PositionedEmote;
import net.blay09.mods.chattweaks.image.ChatImageEmote;
import net.blay09.mods.chattweaks.text.StyledString;
import net.blay09.mods.chattweaks.text.StyledStringSection;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatView {

	public static final Pattern defaultFilterPattern = Pattern.compile("(?:<(?<s>[^>]+)>)? ?(?<m>.*)", Pattern.DOTALL);
	public static final Pattern groupPattern = Pattern.compile("\\$(?:([0-9])|\\{([\\w])\\})");
	public static final Pattern outputFormattingPattern = Pattern.compile("(\\\\~|~[0-9abcdefkolmnr])");
	private static final EmoteScanner emoteScanner = new EmoteScanner();
	private static final int MAX_MESSAGES = 100;

	private String name;
	private final List<ChatChannel> channels = Lists.newArrayList();
	private String filterPattern = "";
	private String outputFormat = "$0";
	private MessageStyle messageStyle = MessageStyle.Chat;
	private String outgoingPrefix;
	private boolean isExclusive;
	private boolean isMuted;

	private Pattern compiledFilterPattern = defaultFilterPattern;
	private String compiledOutputFormat = outputFormat;
	private Matcher lastMatcher;
	private final List<ChatMessage> chatLines = Lists.newArrayList();
	private boolean hasUnread;

	private boolean isTemporary;

	public ChatView(String name) {
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static ChatView fromJson(JsonObject jsonView) {
		ChatView view = new ChatView(jsonView.get("name").getAsString());
		view.setFilterPattern(jsonView.has("filterPattern") ? jsonView.get("filterPattern").getAsString() : "");
		view.setOutputFormat(jsonView.get("outputFormat").getAsString());
		view.setMessageStyle(MessageStyle.valueOf(jsonView.get("style").getAsString()));
		view.setOutgoingPrefix(jsonView.has("outgoingPrefix") ? jsonView.get("outgoingPrefix").getAsString() : null);
		view.setExclusive(jsonView.get("isExclusive").getAsBoolean());
		view.setMuted(jsonView.get("isMuted").getAsBoolean());

		JsonArray channels = jsonView.getAsJsonArray("channels");
		if(channels != null) {
			for (int i = 0; i < channels.size(); i++) {
				JsonElement element = channels.get(i);
				if (!element.isJsonPrimitive()) {
					continue;
				}
				view.addChannel(ChatManager.getChatChannel(element.getAsString()));
			}
		}

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

		JsonArray channels = new JsonArray();
		for(ChatChannel channel : this.channels) {
			channels.add(new JsonPrimitive(channel.getName()));
		}
		object.add("channels", channels);
		return object;
	}

	public boolean messageMatches(String message) {
		lastMatcher = compiledFilterPattern.matcher(message);
		return lastMatcher.matches();
	}

	public ChatMessage addChatLine(ChatMessage chatLine) {
		chatLine = chatLine.copy();
		chatLines.add(chatLine);
		if(chatLines.size() > MAX_MESSAGES) {
			chatLines.remove(0);
		}

		try {
			chatLine.setSender(lastMatcher.group("s"));
			chatLine.setMessage(lastMatcher.group("m"));
		} catch (Exception ignored) {
			chatLine.setMessage(lastMatcher.group(0));
		}

		ITextComponent textComponent = chatLine.getTextComponent();
		if(!compiledOutputFormat.equals("$0")) {
			StyledString styledString = new StyledString(textComponent);
			Matcher matcher = groupPattern.matcher(compiledOutputFormat);
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
					try {
						groupValue = lastMatcher.group(namedGroup);
					} catch (Exception ignored) {
						groupValue = "";
					}
				} else {
					int group = Integer.parseInt(matcher.group(1));
					start = lastMatcher.start(group);
					end = lastMatcher.end(group);
					try {
						groupValue = lastMatcher.group(group);
					} catch (Exception ignored) {
						groupValue = "";
					}
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
		ITextComponent newComponent = null;
		for(ITextComponent component : textComponent) {
			if(component instanceof TextComponentString) {
				String text = ((TextComponentString) component).getText();
				if(text.length() > 1) {
					int index = 0;
					StringBuilder sb = new StringBuilder();
					List<PositionedEmote> emotes = emoteScanner.scanForEmotes(text, null);
					for(PositionedEmote emoteData : emotes) {
						if (index < emoteData.getStart()) {
							sb.append(text.substring(index, emoteData.getStart()));
						}
						int imageIndex = sb.length() + 1;
						sb.append("\u00a7*");
						for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
							sb.append(' ');
						}
						chatLine.addImage(new ChatImageEmote(imageIndex, emoteData.getEmote()));
						index = emoteData.getEnd() + 1;
					}
					if(index < text.length()) {
						sb.append(text.substring(index));
					}
					((TextComponentString) component).text = sb.toString();
				}
				component.getSiblings().clear();
				if(text.length() > 0) {
					if (newComponent == null) {
						newComponent = new TextComponentString("");
						newComponent.setStyle(textComponent.getStyle().createDeepCopy());
					}
					newComponent.appendSibling(component);
				}
			}
		}
		if(newComponent == null) {
			newComponent = textComponent;
		}
		chatLine.setTextComponent(newComponent);
		return chatLine;
	}

	public List<ChatMessage> getChatLines() {
		return chatLines;
	}

	public boolean hasUnreadMessages() {
		return hasUnread;
	}

	public void markAsUnread(boolean hasUnread) {
		this.hasUnread = hasUnread;
	}

	public void addChannel(ChatChannel channel) {
		channels.add(channel);
	}

	public Collection<ChatChannel> getChannels() {
		return channels;
	}

	public void setFilterPattern(String filterPattern) {
		this.filterPattern = filterPattern;
		if(!filterPattern.isEmpty()) {
			try {
				compiledFilterPattern = Pattern.compile(filterPattern, Pattern.DOTALL);
			} catch (PatternSyntaxException e) {
				compiledFilterPattern = defaultFilterPattern;
			}
		} else {
			compiledFilterPattern = defaultFilterPattern;
		}
	}

	public String getFilterPattern() {
		return filterPattern;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
		Matcher matcher = outputFormattingPattern.matcher(outputFormat);
		StringBuffer sb = new StringBuffer();
		while(matcher.find()) {
			matcher.appendReplacement(sb, "\u00a7" + matcher.group(1));
		}
		matcher.appendTail(sb);
		compiledOutputFormat = sb.toString();
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

	@Nullable
	public String getOutgoingPrefix() {
		return outgoingPrefix;
	}

	public void setOutgoingPrefix(@Nullable String outgoingPrefix) {
		this.outgoingPrefix = outgoingPrefix;
	}

	public boolean isTemporary() {
		return isTemporary;
	}

	public void setTemporary(boolean temporary) {
		isTemporary = temporary;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ChatView chatView = (ChatView) o;
		return name.equals(chatView.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
