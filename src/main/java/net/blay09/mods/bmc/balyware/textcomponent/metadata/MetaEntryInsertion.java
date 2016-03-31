package net.blay09.mods.bmc.balyware.textcomponent.metadata;

import net.minecraft.util.text.Style;

public class MetaEntryInsertion extends MetaEntry {

	private final String insertion;

	public MetaEntryInsertion(int index, int length, String insertion) {
		super(index, length);
		this.insertion = insertion;
	}

	@Override
	public MetaEntry copy(int index) {
		return new MetaEntryInsertion(index, length, insertion);
	}

	@Override
	public void apply(Style style) {
		style.setInsertion(insertion);
	}
}
