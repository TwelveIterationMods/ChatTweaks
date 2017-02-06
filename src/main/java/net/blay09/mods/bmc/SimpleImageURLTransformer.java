package net.blay09.mods.bmc;

import com.google.common.base.Function;

import javax.annotation.Nullable;

public class SimpleImageURLTransformer implements Function<String, String> {

	private final String pattern;
	private final String suffix;

	public SimpleImageURLTransformer(String pattern, String suffix) {
		this.pattern = pattern;
		this.suffix = suffix;
	}

	@Override
	public String apply(@Nullable String input) {
		if(input != null && input.matches(pattern)) {
			return input + suffix;
		}
		return null;
	}
}
