package net.blay09.mods.bmc.balyware.textcomponent.metadata;

import net.minecraft.util.text.Style;

public abstract class MetaEntry implements Comparable<MetaEntry> {
	private final int index;
	protected final int length;

	public MetaEntry(int index, int length) {
		this.index = index;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public int getIndex() {
		return index;
	}

	public abstract MetaEntry copy(int index);

	@Override
	public int compareTo(MetaEntry o) {
		return hashCode();
	}

	public abstract void apply(Style style);
}
