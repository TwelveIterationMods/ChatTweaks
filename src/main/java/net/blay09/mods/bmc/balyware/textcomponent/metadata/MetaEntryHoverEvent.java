package net.blay09.mods.bmc.balyware.textcomponent.metadata;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;

public class MetaEntryHoverEvent extends MetaEntry {

	private final HoverEvent hoverEvent;

	public MetaEntryHoverEvent(int index, int length, HoverEvent hoverEvent) {
		super(index, length);
		this.hoverEvent = hoverEvent;
	}

	@Override
	public MetaEntry copy(int index) {
		return new MetaEntryHoverEvent(index, length, hoverEvent);
	}

	@Override
	public void apply(Style style) {
		style.setHoverEvent(hoverEvent);
	}
}
