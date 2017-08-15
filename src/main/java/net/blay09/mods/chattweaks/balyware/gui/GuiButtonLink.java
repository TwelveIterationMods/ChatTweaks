package net.blay09.mods.chattweaks.balyware.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonLink extends GuiButton {

	public GuiButtonLink(int buttonId, int x, int y, FontRenderer fontRenderer, String text) {
		super(buttonId, x, y, fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT, text);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			int hoverState = getHoverState(hovered);
			mc.fontRenderer.drawStringWithShadow(displayString, x, y, hoverState == 2 ? 0xFFFFFF : 0xBBBBBB);
		}
	}

}
