package net.blay09.mods.bmc.image;

import java.util.Collections;
import java.util.List;

public abstract class ChatImage {

    private int index;

    protected ChatImage(int index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getSpaces() {
        return 4;
    }

    public float getScale() {
        return 1f;
    }

	public List<String> getTooltip() {
		return Collections.emptyList();
	}

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract void draw(int x, int y, int alpha);
}
