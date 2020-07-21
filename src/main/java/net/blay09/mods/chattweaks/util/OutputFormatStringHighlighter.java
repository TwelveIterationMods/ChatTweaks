package net.blay09.mods.chattweaks.util;

import net.minecraft.util.text.TextFormatting;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutputFormatStringHighlighter implements Function<String, String> {

    private static final Matcher VARIABLES = Pattern.compile("(\\$\\{\\w+\\}|\\$0)").matcher("");
    private static final Matcher FORMATTING = Pattern.compile("(?<=(?:^|[^\\\\]))(~[0-9abcdefklmnor])").matcher("");
    private static final Matcher ESCAPED = Pattern.compile("(\\\\~)").matcher("");

    @Override
    public String apply(String text) {
        VARIABLES.reset(text);
        StringBuffer sb = new StringBuffer();
        while (VARIABLES.find()) {
            VARIABLES.appendReplacement(sb, TextFormatting.GREEN + "\\" + VARIABLES.group(0) + TextFormatting.WHITE);
        }
        VARIABLES.appendTail(sb);

        FORMATTING.reset(sb.toString());
        sb = new StringBuffer();
        while (FORMATTING.find()) {
            FORMATTING.appendReplacement(sb, TextFormatting.GOLD + "\\" + FORMATTING.group(0) + TextFormatting.WHITE);
        }
        FORMATTING.appendTail(sb);

        ESCAPED.reset(sb.toString());
        sb = new StringBuffer();
        while (ESCAPED.find()) {
            ESCAPED.appendReplacement(sb, TextFormatting.LIGHT_PURPLE + "\\" + ESCAPED.group(0) + TextFormatting.WHITE);
        }
        ESCAPED.appendTail(sb);
        return sb.toString();
    }

}
