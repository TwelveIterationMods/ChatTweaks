package net.blay09.mods.chattweaks.chat;

public class TextRenderRegion {

	private final String text;
	private final int color;

	public TextRenderRegion(String text, int color) {
		this.text = text;
		this.color = color;
	}

	public String getText() {
		return text;
	}

	public int getColor() {
		return color;
	}
}
