package net.blay09.mods.bmc.image;

import net.blay09.mods.bmc.api.image.IAnimatedChatRenderable;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.List;

public class ChatImageEmote extends ChatImage {

	private final int spaces;
	private final IEmote emote;

	public ChatImageEmote(int index, IEmote emote) {
		super(index);
		this.emote = emote;
		this.spaces = emote.getWidthInSpaces();
	}

	@Override
	public void draw(int x, int y, int alpha) {
		IChatRenderable image = emote.getImage();
		int textureId = image != null ? image.getTextureId() : -1;
		if(textureId == -1) {
			emote.requestLoad();
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.bindTexture(textureId);
		GlStateManager.color(1f, 1f, 1f, (float) alpha / 255f);
		GlStateManager.translate(x, y, 100f);
		if(emote.getImage() instanceof IAnimatedChatRenderable) {
			((IAnimatedChatRenderable) emote.getImage()).updateAnimation();
		}
		Gui.drawModalRectWithCustomSizedTexture(0, 0, image.getTexCoordX(), image.getTexCoordY(), image.getWidth(), image.getHeight(), image.getSheetWidth(), image.getSheetHeight());
		GlStateManager.popMatrix();
	}

	@Override
	public int getWidth() {
		return emote.getImage().getWidth();
	}

	@Override
	public int getHeight() {
		return emote.getImage().getHeight();
	}

	@Override
	public float getScale() {
		return emote.getImage().getScale();
	}

	@Override
	public int getSpaces() {
		return spaces;
	}

	@Override
	public List<String> getTooltip() {
		return emote.getTooltip();
	}
}
