package net.blay09.mods.chattweaks;

import javax.annotation.Nullable;
import java.util.function.Function;

public class SimpleImageURLTransformer implements Function<String, String> {

	private final String pattern;
	private final String suffix;

	public SimpleImageURLTransformer(String pattern, String suffix) {
		this.pattern = pattern;
		this.suffix = suffix;
	}

	@Override
	@Nullable
	public String apply(@Nullable String input) {
		if(input != null && input.matches(pattern)) {
			return input + suffix;
		}
		return null;
	}
}
