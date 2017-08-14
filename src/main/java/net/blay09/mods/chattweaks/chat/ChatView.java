package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.chattweaks.ChatManager;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.chat.emotes.EmoteScanner;
import net.blay09.mods.chattweaks.chat.emotes.PositionedEmote;
import net.blay09.mods.chattweaks.image.ChatImageEmote;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
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
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("[HH:mm] ");

	private String name;
	private final List<ChatChannel> channels = Lists.newArrayList();
	private String filterPattern = "";
	private String outputFormat = "$0";
	private MessageStyle messageStyle = MessageStyle.Chat;
	private String outgoingPrefix;
	private boolean isExclusive;
	private boolean isMuted;

	private Pattern compiledFilterPattern = defaultFilterPattern;
	private String builtOutputFormat = outputFormat;
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
		if (channels != null) {
			for (int i = 0; i < channels.size(); i++) {
				JsonElement element = channels.get(i);
				if (!element.isJsonPrimitive()) {
					continue;
				}
				ChatChannel channel = ChatManager.getChatChannel(element.getAsString());
				if (channel != null) {
					view.addChannel(channel);
				} else {
					ChatTweaks.logger.error("Channel {} does no longer exist. Removing it from view {}.", element.getAsString(), view.name);
				}
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
		for (ChatChannel channel : this.channels) {
			channels.add(new JsonPrimitive(channel.getName()));
		}
		object.add("channels", channels);
		return object;
	}

	public boolean messageMatches(String message) {
		lastMatcher = compiledFilterPattern.matcher(message);
		return lastMatcher.matches();
	}

	private ITextComponent subTextComponent(ITextComponent component, int startIndex, int endIndex) {
		int index = 0;
		ITextComponent result = new TextComponentString("");
		for(ITextComponent part : component) {
			String unformatted = part.getUnformattedComponentText();
			int min = Math.max(0, startIndex - index);
			int max = Math.min(endIndex - index, unformatted.length());
			if(unformatted.length() >= min && max > min) {
				String sub = unformatted.substring(min, max);
				if(sub.length() > 0) {
					ITextComponent sibling = new TextComponentString(sub);
					sibling.setStyle(part.getStyle());
					result.appendSibling(sibling);
				}
			}
			index += unformatted.length();
		}
		return result;
	}

	public ChatMessage addChatLine(ChatMessage chatLine) {
		chatLine = chatLine.copy();
		chatLines.add(chatLine);
		if (chatLines.size() > MAX_MESSAGES) {
			chatLines.remove(0);
		}

		try {
			if (chatLine.getSender() == null) {
				chatLine.setSender(subTextComponent(chatLine.getTextComponent(), lastMatcher.start("s"), lastMatcher.end("s")));
			}
			if (chatLine.getMessage() == null) {
				chatLine.setMessage(subTextComponent(chatLine.getTextComponent(), lastMatcher.start("m"), lastMatcher.end("m")));
			}
		} catch (IllegalArgumentException ignored) {
			if (chatLine.getMessage() == null) {
				chatLine.setMessage(chatLine.getTextComponent());
			}
		}

		ITextComponent source = chatLine.getTextComponent();
		ITextComponent textComponent = chatLine.getTextComponent();
		if (!builtOutputFormat.equals("$0")) {
			textComponent = new TextComponentString("");
			int last = 0;
			Matcher matcher = groupPattern.matcher(builtOutputFormat);
			while (matcher.find()) {
				if(matcher.start() > last) {
					textComponent.appendText(builtOutputFormat.substring(last, matcher.start()));
				}

				ITextComponent groupValue = null;
				String namedGroup = matcher.group(2);
				if (namedGroup != null) {
					if (namedGroup.equals("s") && chatLine.getSender() != null) {
						groupValue = chatLine.getSender();
					} else if (namedGroup.equals("m") && chatLine.getMessage() != null) {
						groupValue = chatLine.getMessage();
					} else if(namedGroup.equals("t")) {
						groupValue = new TextComponentString(DATE_FORMAT.format(new Date(chatLine.getTimestamp())));
						groupValue.getStyle().setColor(TextFormatting.GRAY);
					} else {
						int groupStart = -1;
						int groupEnd = -1;
						try {
							groupStart = lastMatcher.start(namedGroup);
							groupEnd = lastMatcher.end(namedGroup);
						} catch (IllegalArgumentException ignored) {}
						if(groupStart != -1 && groupEnd != -1) {
							groupValue = subTextComponent(source, groupStart, groupEnd);
						} else {
							groupValue = chatLine.getOutputVar(namedGroup);
						}
					}
				} else {
					int group = Integer.parseInt(matcher.group(1));
					if(group >= 0 && group <= lastMatcher.groupCount()) {
						groupValue = subTextComponent(source, lastMatcher.start(group), lastMatcher.end(group));
					}
				}

				if (groupValue == null) {
					groupValue = new TextComponentString("missingno");
				}

				last = matcher.end();
				textComponent.appendSibling(groupValue);
			}

			if(last < builtOutputFormat.length()) {
				textComponent.appendText(builtOutputFormat.substring(last, builtOutputFormat.length()));
			}
		}

		ITextComponent newComponent = null;
		for (ITextComponent component : textComponent) {
			if (component instanceof TextComponentString) {
				String text = ((TextComponentString) component).getText();
				if (text.length() > 1) {
					int index = 0;
					StringBuilder sb = new StringBuilder();
					List<PositionedEmote> emotes = emoteScanner.scanForEmotes(text, null);
					for (PositionedEmote emoteData : emotes) {
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
					if (index < text.length()) {
						sb.append(text.substring(index));
					}
					((TextComponentString) component).text = sb.toString();
				}
				if (text.length() > 0) {
					if (newComponent == null) {
						newComponent = new TextComponentString("");
						newComponent.setStyle(textComponent.getStyle().createDeepCopy());
					}
					TextComponentString copyComponent = new TextComponentString(((TextComponentString) component).text);
					copyComponent.setStyle(component.getStyle());
					newComponent.appendSibling(copyComponent);
				}
			}
		}
		if (newComponent == null) {
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
		if (!filterPattern.isEmpty()) {
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
		while (matcher.find()) {
			matcher.appendReplacement(sb, "\u00a7" + matcher.group(1));
		}
		matcher.appendTail(sb);
		builtOutputFormat = sb.toString();
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

	public void refresh() {
		chatLines.clear();
		for (ChatChannel chatChannel : channels) {
			for (ChatMessage chatMessage : chatChannel.getChatMessages()) {
				if (messageMatches(chatMessage.getTextComponent().getUnformattedText())) {
					addChatLine(chatMessage);
				}
			}
		}
		chatLines.sort(Comparator.comparingInt(ChatMessage::getId));
	}
}
