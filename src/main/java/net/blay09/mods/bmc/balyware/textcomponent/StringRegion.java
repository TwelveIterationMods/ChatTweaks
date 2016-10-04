package net.blay09.mods.bmc.balyware.textcomponent;

import javax.annotation.Nullable;

public class StringRegion implements Comparable<StringRegion> {

	private final int index;
	private final int length;

	public StringRegion(int index, int length) {
		this.index = index;
		this.length = length;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StringRegion that = (StringRegion) o;
		return index == that.index && length == that.length;

	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + length;
		return result;
	}

	public int getIndex() {
		return index;
	}

	public int getLength() {
		return length;
	}


	@Override
	public int compareTo(StringRegion o) {
		return index - o.index;
	}
}
