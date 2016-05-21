package net.blay09.mods.bmc.balyware.textcomponent.metadata;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

public class MetaEntryClickEvent extends MetaEntry {

	private final ClickEvent clickEvent;

	public MetaEntryClickEvent(int index, int length, ClickEvent clickEvent) {
		super(index, length);
		this.clickEvent = clickEvent;
	}

	@Override
	public MetaEntry copy(int index) {
		return new MetaEntryClickEvent(index, length, clickEvent);
	}

	@Override
	public void apply(Style style) {
		style.setClickEvent(clickEvent);
	}
}
