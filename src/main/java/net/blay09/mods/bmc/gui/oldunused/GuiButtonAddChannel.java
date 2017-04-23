package net.blay09.mods.bmc.gui.oldunused;

import net.blay09.mods.bmc.ChatTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonAddChannel extends GuiButton {

	protected static final ResourceLocation texture = new ResourceLocation(ChatTweaks.MOD_ID, "gui.png");

	public GuiButtonAddChannel(int buttonId, int x, int y) {
		super(buttonId, x, y, 12, 12, "");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			int hoverState = getHoverState(hovered);
			mc.getTextureManager().bindTexture(texture);
			GlStateManager.color(1f, 1f, 1f, 1f);
			Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 24, hoverState == 2 ? 28 : (hoverState == 1 ? 16 : 40), 12, 12, 128, 64);
		}
	}

}
