package net.blay09.mods.bmc.balyware.textcomponent.metadata;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class MetaEntryFormatting extends MetaEntry {

	private final TextFormatting formatting;

	public MetaEntryFormatting(int index, int length, TextFormatting formatting) {
		super(index, length);
		this.formatting = formatting;
	}

	@Override
	public MetaEntry copy(int index) {
		return new MetaEntryFormatting(index, length, formatting);
	}

	@Override
	public void apply(Style style) {
		switch(formatting) {
			case OBFUSCATED:
				style.setObfuscated(true);
				break;
			case BOLD:
				style.setBold(true);
				break;
			case STRIKETHROUGH:
				style.setStrikethrough(true);
				break;
			case UNDERLINE:
				style.setUnderlined(true);
				break;
			case ITALIC:
				style.setItalic(true);
				break;
			default:
				style.setColor(formatting);
		}
	}
}
