package net.blay09.mods.bmc.balyware.textcomponent;

import com.google.common.collect.TreeMultimap;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class StyleExtractor extends TextComponentTransformer {

	private int offset;
	private TreeMultimap<StringRegion, MetaEntry> metadata;

	@Override
	public void begin(ITextComponent chatComponent) {
		offset = 0;
		metadata = TreeMultimap.create();
	}

	@Override
	public ITextComponent transformStyle(ITextComponent component) {
		int start = offset;
		int length = component.getUnformattedText().length();
		if(length == 0) {
			return component;
		}
		offset += length;
		Style style = component.getChatStyle();
		if (style.getColor() != null || style.getInsertion() != null || style.getStrikethrough() || style.getBold() || style.getItalic() || style.getUnderlined() || style.getObfuscated() || style.getChatClickEvent() != null || style.getChatHoverEvent() != null) {
			if (style.getChatClickEvent() != null) {
				metadata.put(new StringRegion(start, length), new MetaEntryClickEvent(start, length, style.getChatClickEvent()));
			}
			if (style.getChatHoverEvent() != null) {
				metadata.put(new StringRegion(start, length), new MetaEntryHoverEvent(start, length, style.getChatHoverEvent()));
			}
			if (style.getBold()) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, TextFormatting.BOLD));
			}
			if (style.getItalic()) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, TextFormatting.ITALIC));
			}
			if (style.getUnderlined()) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, TextFormatting.UNDERLINE));
			}
			if (style.getObfuscated()) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, TextFormatting.OBFUSCATED));
			}
			if (style.getStrikethrough()) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, TextFormatting.STRIKETHROUGH));
			}
			if (style.getInsertion() != null) {
				metadata.put(new StringRegion(start, length), new MetaEntryInsertion(start, length, style.getInsertion()));
			}
			if (style.getColor() != null) {
				metadata.put(new StringRegion(start, length), new MetaEntryFormatting(start, length, style.getColor()));
			}
		}
		return component;
	}

	public TreeMultimap<StringRegion, MetaEntry> getMetadata() {
		return metadata;
	}
}
