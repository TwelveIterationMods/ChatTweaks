package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.chattweaks.ChatManager;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatView {

    public static final Pattern defaultFilterPattern = Pattern.compile("(?:<(?<s>[^>]+)>)? ?(?<m>.*)", Pattern.DOTALL);
    public static final Pattern groupPattern = Pattern.compile("\\$(?:([0-9])|\\{([\\w])\\})");
    public static final Pattern outputFormattingPattern = Pattern.compile("(\\\\~|~[0-9abcdefkolmnr])");

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
        Matcher matcher = compiledFilterPattern.matcher(message);
        return matcher.matches();
    }

    private ITextComponent subTextComponent(ITextComponent component, int startIndex, int endIndex) {
        int index = 0;
        TextComponent result = new StringTextComponent("");
        for (ITextComponent part : component) {
            String unformatted = part.getUnformattedComponentText();
            int min = Math.max(0, startIndex - index);
            int max = Math.min(endIndex - index, unformatted.length());
            if (unformatted.length() >= min && max > min) {
                String sub = unformatted.substring(min, max);
                if (sub.length() > 0) {
                    TextComponent sibling = new StringTextComponent(sub);
                    sibling.func_230530_a_(part.getStyle()); // setStyle
                    result.func_230529_a_(sibling); // appendSibling
                }
            }
            index += unformatted.length();
        }
        return result;
    }

    public ChatMessage addChatLine(ChatMessage chatLine) {
        chatLine = chatLine.copy();
        chatLines.add(chatLine);
        if (chatLines.size() > ChatTweaks.MAX_MESSAGES) {
            chatLines.remove(0);
        }

        Matcher matcher = compiledFilterPattern.matcher(chatLine.getTextComponent().getString());
        if (!matcher.matches()) {
            return chatLine;
        }

        try {
            if (chatLine.getSender() == null) {
                chatLine.setSender(subTextComponent(chatLine.getTextComponent(), matcher.start("s"), matcher.end("s")));
            }
            if (chatLine.getMessage() == null) {
                chatLine.setMessage(subTextComponent(chatLine.getTextComponent(), matcher.start("m"), matcher.end("m")));
            }
        } catch (IllegalArgumentException ignored) {
            if (chatLine.getMessage() == null) {
                chatLine.setMessage(chatLine.getTextComponent());
            }
        }

        ITextComponent source = chatLine.getTextComponent();
        ITextComponent textComponent = chatLine.getTextComponent();
        if (!builtOutputFormat.equals("$0")) {
            TextComponent formattedTextComponent = new StringTextComponent("");
            int last = 0;
            Matcher outputMatcher = groupPattern.matcher(builtOutputFormat);
            while (outputMatcher.find()) {
                if (outputMatcher.start() > last) {
                    formattedTextComponent.func_240702_b_(builtOutputFormat.substring(last, outputMatcher.start())); // appendString
                }

                ITextComponent groupValue = null;
                String namedGroup = outputMatcher.group(2);
                if (namedGroup != null) {
                    if (namedGroup.equals("s") && chatLine.getSender() != null) {
                        groupValue = chatLine.getSender();
                    } else if (namedGroup.equals("m") && chatLine.getMessage() != null) {
                        groupValue = chatLine.getMessage();
                    } else if (namedGroup.equals("t")) {
                        final StringTextComponent timeStampComponent = new StringTextComponent(ChatTweaksConfig.cachedTimestampFormat.format(new Date(chatLine.getTimestamp())));
                        timeStampComponent.func_240699_a_(TextFormatting.GRAY);
                        groupValue = timeStampComponent;
                    } else {
                        int groupStart = -1;
                        int groupEnd = -1;
                        try {
                            groupStart = matcher.start(namedGroup);
                            groupEnd = matcher.end(namedGroup);
                        } catch (IllegalArgumentException ignored) {
                        }
                        if (groupStart != -1 && groupEnd != -1) {
                            groupValue = subTextComponent(source, groupStart, groupEnd);
                        } else {
                            groupValue = chatLine.getOutputVar(namedGroup);
                        }
                    }
                } else {
                    int group = Integer.parseInt(outputMatcher.group(1));
                    if (group >= 0 && group <= matcher.groupCount()) {
                        groupValue = subTextComponent(source, matcher.start(group), matcher.end(group));
                    }
                }

                if (groupValue == null) {
                    groupValue = new StringTextComponent("*");
                }

                last = outputMatcher.end();
                formattedTextComponent.func_230529_a_(groupValue); // appendSibling
            }

            if (last < builtOutputFormat.length()) {
                formattedTextComponent.func_240702_b_(builtOutputFormat.substring(last)); // appendString
            }

            textComponent = formattedTextComponent;
        }

        TextComponent resultComponent = null;
        for (ITextComponent component : textComponent) {
            String text = component.getUnformattedComponentText();
            if (text.length() > 0) {
                if (resultComponent == null) {
                    resultComponent = new StringTextComponent("");
                    resultComponent.func_230530_a_(textComponent.getStyle()); // setStyle
                }

                StringTextComponent copyComponent = new StringTextComponent(text);
                copyComponent.func_230530_a_(component.getStyle()); // setStyle
                resultComponent.func_230529_a_(copyComponent); // appendSibling
            }
        }

        chatLine.setTextComponent(resultComponent != null ? resultComponent : textComponent);
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

        channels.stream()
                .flatMap(it -> it.getChatMessages().stream())
                .filter(it -> messageMatches(it.getTextComponent().getString()))
                .sorted(Comparator.comparingInt(ChatMessage::getId).reversed())
                .limit(ChatTweaks.MAX_MESSAGES)
                .sorted(Comparator.comparingInt(ChatMessage::getId))
                .forEach(this::addChatLine);
    }
}
