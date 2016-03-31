package net.blay09.mods.bmc.image;

import net.blay09.mods.bmc.api.image.IAnimatedChatRenderable;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class ChatImageDefault extends ChatImage {

	private final int spaces;
	private final IChatRenderable image;
	private final ITooltipProvider tooltip;

	public ChatImageDefault(int index, IChatRenderable image, ITooltipProvider tooltip) {
		super(index);
		this.image = image;
		this.tooltip = tooltip;
		this.spaces = image.getWidthInSpaces();
	}

	@Override
	public void draw(int x, int y, int alpha) {
		int textureId = image != null ? image.getTextureId() : -1;
		if(textureId == -1) {
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(textureId);
		GlStateManager.color(1f, 1f, 1f, (float) alpha / 255f);
		GlStateManager.translate(x, y, 100f);
		if(image instanceof IAnimatedChatRenderable) {
			((IAnimatedChatRenderable) image).updateAnimation();
		}
		Gui.drawModalRectWithCustomSizedTexture(0, 0, image.getTexCoordX(), image.getTexCoordY(), image.getWidth(), image.getHeight(), image.getSheetWidth(), image.getSheetHeight());
		GlStateManager.popMatrix();
	}

	@Override
	public int getWidth() {
		return image.getWidth();
	}

	@Override
	public int getHeight() {
		return image.getHeight();
	}

	@Override
	public float getScale() {
		return image.getScale();
	}

	@Override
	public int getSpaces() {
		return spaces;
	}

	@Override
	public List<String> getTooltip() {
		return tooltip.getTooltip();
	}
}
