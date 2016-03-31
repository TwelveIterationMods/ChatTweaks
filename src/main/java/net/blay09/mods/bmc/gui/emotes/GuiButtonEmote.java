package net.blay09.mods.bmc.gui.emotes;

import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.image.IAnimatedChatRenderable;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

public class GuiButtonEmote extends GuiButton {

	private final IEmote emote;

	public GuiButtonEmote(int buttonId, int x, int y, IEmote emote) {
		super(buttonId, x, y, 18, 12, "");
		this.emote = emote;
		this.visible = false;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int hoverState = getHoverState(hovered);
			mouseDragged(mc, mouseX, mouseY);
			Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, (hoverState == 2) ? 0x88333333 : 0x44000000);
			IChatRenderable image = emote.getImage();
			if(image.getTextureId() != -1) {
				GlStateManager.pushMatrix();
				GlStateManager.enableAlpha();
				GlStateManager.enableBlend();
				GlStateManager.bindTexture(image.getTextureId());
				GlStateManager.color(1f, 1f, 1f, 1f);
				GlStateManager.translate(xPosition + width / 2 - (image.getWidth() * image.getScale()) / 2, yPosition, 100f);
				GlStateManager.scale(image.getScale(), image.getScale(), 1f);
				if(image instanceof IAnimatedChatRenderable) {
					((IAnimatedChatRenderable) image).updateAnimation();
				}
				Gui.drawModalRectWithCustomSizedTexture(0, 0, image.getTexCoordX(), image.getTexCoordY(), image.getWidth(), image.getHeight(), image.getSheetWidth(), image.getSheetHeight());
				GlStateManager.popMatrix();
			} else {
				emote.requestLoad();
			}
		}
	}

	public IEmote getEmote() {
		return emote;
	}
}
