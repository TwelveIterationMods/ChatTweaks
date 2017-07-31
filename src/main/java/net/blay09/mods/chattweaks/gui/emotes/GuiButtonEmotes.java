package net.blay09.mods.chattweaks.gui.emotes;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonEmotes extends GuiButton {

	public GuiButtonEmotes(int buttonId, int x, int y) {
		super(buttonId, x, y, 14, 12, "\u263a");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRenderer;
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
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
			Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, (hoverState == 2) ? 0x88333333 : 0x44000000);
			drawCenteredString(fondRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, j);
		}
	}
}
