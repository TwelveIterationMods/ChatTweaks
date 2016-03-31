package net.blay09.mods.bmc.image.renderable;

import net.blay09.mods.bmc.api.image.IChatRenderable;

public class NullRenderable implements IChatRenderable {

	public static final NullRenderable INSTANCE = new NullRenderable();

	@Override
	public int getWidthInSpaces() {
		return 4;
	}

	@Override
	public int getTextureId() {
		return -1;
	}

	@Override
	public void disposeTexture() {}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public float getScale() {
		return 1f;
	}

	@Override
	public void setScale(float scale) {}

	@Override
	public int getTexCoordX() {
		return 0;
	}

	@Override
	public int getTexCoordY() {
		return 0;
	}

	@Override
	public int getSheetWidth() {
		return 0;
	}

	@Override
	public int getSheetHeight() {
		return 0;
	}
}
