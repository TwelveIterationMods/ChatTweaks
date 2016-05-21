package net.blay09.mods.bmc.image;

import net.blay09.mods.bmc.api.image.IChatImage;

import java.util.Collections;
import java.util.List;

public abstract class ChatImage implements IChatImage {

    private int index;

    protected ChatImage(int index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getSpaces() {
        return 4;
    }

    @Override
    public float getScale() {
        return 1f;
    }

    @Override
	public List<String> getTooltip() {
		return Collections.emptyList();
	}
}
