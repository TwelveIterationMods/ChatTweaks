package net.blay09.mods.chattweaks.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonSettings extends GuiButton {

	public GuiButtonSettings(int buttonId, int x, int y) {
		super(buttonId, x, y, 14, 12, "...");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRenderer;
			this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			int hoverState = getHoverState(hovered);
			mouseDragged(mc, mouseX, mouseY);
			int j = 0xE0E0E0;
			if (packedFGColour != 0) {
				j = packedFGColour;
			} else if (!enabled) {
				j = 0xA0A0A0;
			} else if (hovered) {
				j = 0xFFFFA0;
			}
			Gui.drawRect(x, y, x + width, y + height, (hoverState == 2) ? 0x44333333 : 0x44000000);
			drawCenteredString(fondRenderer, displayString, x + width / 2, y + (height - 8) / 2, j);
		}
	}
}