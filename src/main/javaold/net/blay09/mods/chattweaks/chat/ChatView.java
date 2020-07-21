package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChatView {

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

}
