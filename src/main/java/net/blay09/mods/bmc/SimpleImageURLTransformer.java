package net.blay09.mods.bmc;

import com.google.common.base.Function;

public class SimpleImageURLTransformer implements Function<String, String> {

	private final String pattern;
	private final String suffix;

	public SimpleImageURLTransformer(String pattern, String suffix) {
		this.pattern = pattern;
		this.suffix = suffix;
	}

	@Override
	public String apply(String input) {
		if(input.matches(pattern)) {
			return input + suffix;
		}
		return null;
	}
}
