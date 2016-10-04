package net.blay09.mods.bmc.text;

import net.minecraft.util.text.Style;

public class StyledStringSection implements Comparable<StyledStringSection> {
	private final int start;
	private final int end;
	private final Style style;

	public StyledStringSection(int start, int end, Style style) {
		this.start = start;
		this.end = end;
		this.style = style;
	}

	@Override
	public int compareTo(StyledStringSection other) {
		return this.start - other.start;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public Style getStyle() {
		return style;
	}
}
