package net.blay09.mods.chattweaks.gui.formatter;

import net.blay09.mods.chattweaks.gui.formatter.IStringFormatter;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExStringFormatter implements IStringFormatter {

	private static final Matcher GROUPS = Pattern.compile("(?<=(?:^|[^\\\\]))([\\(\\)\\^]+|\\(\\?:)").matcher("");
	private static final Matcher QUANTIFIERS = Pattern.compile("(?<=(?:^|[^\\\\]))([\\+\\?\\*]+)").matcher("");
	private static final Matcher CLASSES = Pattern.compile("(?<=(?:^|[^\\\\]))([\\[\\]\\^\\.]+)").matcher("");
	private static final Matcher ESCAPED = Pattern.compile("(\\\\.)").matcher("");

	@Override
	public String applyFormatting(String text) {
		GROUPS.reset(text);
		StringBuffer sb = new StringBuffer();
		while (GROUPS.find()) {
			GROUPS.appendReplacement(sb, TextFormatting.GREEN + GROUPS.group(1) + TextFormatting.WHITE);
		}
		GROUPS.appendTail(sb);

		QUANTIFIERS.reset(sb.toString());
		sb = new StringBuffer();
		while (QUANTIFIERS.find()) {
			QUANTIFIERS.appendReplacement(sb, TextFormatting.BLUE + QUANTIFIERS.group(1) + TextFormatting.WHITE);
		}
		QUANTIFIERS.appendTail(sb);

		CLASSES.reset(sb.toString());
		sb = new StringBuffer();
		while (CLASSES.find()) {
			CLASSES.appendReplacement(sb, TextFormatting.GOLD + CLASSES.group(1) + TextFormatting.WHITE);
		}
		CLASSES.appendTail(sb);

		ESCAPED.reset(sb.toString());
		sb = new StringBuffer();
		while (ESCAPED.find()) {
			ESCAPED.appendReplacement(sb, TextFormatting.LIGHT_PURPLE + "\\\\" + ESCAPED.group(1) + TextFormatting.WHITE);
		}
		ESCAPED.appendTail(sb);
		text = sb.toString();

		return text;
	}

}
