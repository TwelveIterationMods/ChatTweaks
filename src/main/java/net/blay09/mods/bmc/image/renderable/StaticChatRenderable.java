package net.blay09.mods.bmc.image.renderable;

import net.blay09.mods.bmc.ChatTweaksConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.image.BufferedImage;

public class StaticChatRenderable implements IChatRenderable {

    private static final float MAX_WIDTH = 128;
    private static final float MAX_HEIGHT = 14;
    private static final float MAX_HEIGHT_SMALL = 8;

    private int textureId = -1;

    protected int width;
	protected int height;
	protected float scale = 1f;
	protected BufferedImage loadBuffer;

	protected StaticChatRenderable() {}

	public StaticChatRenderable(BufferedImage image) {
		width = image.getWidth();
		height = image.getHeight();
		calculateScale();
		loadBuffer = image;
	}

	protected void calculateScale() {
		float renderWidth = width;
		float renderHeight = height;
		if(renderWidth > MAX_WIDTH) {
			float factor = MAX_WIDTH / renderWidth;
			renderWidth *= factor;
			renderHeight *= factor;
		}
		final float maxHeight = ChatTweaksConfig.smallerEmotes ? MAX_HEIGHT_SMALL : MAX_HEIGHT;
		if(renderHeight > maxHeight) {
			float factor = maxHeight / renderHeight;
			renderWidth *= factor;
			renderHeight *= factor;
		}
		float scaleX = renderWidth / width;
		float scaleY = renderHeight / height;
		scale = Math.min(scaleX, scaleY);
	}

	@Override
    public int getWidthInSpaces() {
		if(textureId == -1) {
			return 4; // Texture is not loaded yet - most emotes fit just fine into four spaces though.
		}
        return (int) Math.ceil((width * scale) / (float) Minecraft.getMinecraft().fontRendererObj.getCharWidth(' '));
    }

	@Override
	public int getTextureId() {
		if(loadBuffer != null) {
			textureId = TextureUtil.uploadTextureImage(TextureUtil.glGenTextures(), loadBuffer);
			loadBuffer = null;
		}
		return textureId;
	}

	@Override
	public void disposeTexture() {
		if(textureId != -1) {
			TextureUtil.deleteTexture(textureId);
		}
	}

	@Override
    public int getWidth() {
        return width;
    }

	@Override
    public int getHeight() {
        return height;
    }

	@Override
    public float getScale() {
        return scale;
    }

	@Override
	public void setScale(float scale) {
		this.scale = scale;
	}

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
		return width;
	}

	@Override
	public int getSheetHeight() {
		return height;
	}
}
