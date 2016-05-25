package net.blay09.mods.bmc.balyware.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonLink extends GuiButton {

	public GuiButtonLink(int buttonId, int x, int y, FontRenderer fontRenderer, String text) {
		super(buttonId, x, y, fontRenderer.getStringWidth(text), fontRenderer.FONT_HEIGHT, text);
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			int hoverState = getHoverState(hovered);
			mc.fontRendererObj.drawStringWithShadow(displayString, xPosition, yPosition, hoverState == 2 ? 0xFFFFFF : 0xBBBBBB);
		}
	}

}
