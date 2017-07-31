package net.blay09.mods.chattweaks;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternImageURLTransformer implements Function<String, String> {

	private final Pattern pattern;
	private final String format;

	public PatternImageURLTransformer(String pattern, String format) {
		this.pattern = Pattern.compile(pattern);
		this.format = format;
	}

	public PatternImageURLTransformer(Pattern pattern, String format) {
		this.pattern = pattern;
		this.format = format;
	}

	@Override
	@Nullable
	public String apply(String input) {
		Matcher matcher = pattern.matcher(input);
		if(matcher.matches()) {
			if(matcher.groupCount() > 0) {
				return String.format(format, matcher.group(1));
			} else {
				return String.format(format, input);
			}
		}
		return null;
	}
}
