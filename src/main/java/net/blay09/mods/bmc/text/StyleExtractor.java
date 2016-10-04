package net.blay09.mods.bmc.text;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultimap;
import net.blay09.mods.bmc.balyware.textcomponent.StringRegion;
import net.blay09.mods.bmc.balyware.textcomponent.TextComponentTransformer;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntry;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntryClickEvent;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntryFormatting;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntryHoverEvent;
import net.blay09.mods.bmc.balyware.textcomponent.metadata.MetaEntryInsertion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class StyleExtractor extends TextComponentTransformer {

	private int offset;
	private List<StyledStringSection> sections;

	@Override
	public void begin(ITextComponent chatComponent) {
		offset = 0;
		sections = Lists.newArrayList();
	}

	@Override
	public ITextComponent transformStyle(ITextComponent component) {
		int start = offset;
		int length = component.getUnformattedText().length();
		if(length == 0) {
			return component;
		}
		offset += length;
		sections.add(new StyledStringSection(start, length, component.getStyle()));
		return component;
	}

	public List<StyledStringSection> getSections() {
		return sections;
	}
}
