package net.blay09.mods.bmc.gui.oldunused;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonAddChannelTab extends GuiButton {

	public GuiButtonAddChannelTab(int buttonId, int x, int y, FontRenderer fontRenderer) {
		super(buttonId, x, y, fontRenderer.getStringWidth("[+]"), 10, "[+]");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRenderer;
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			int hoverState = getHoverState(hovered);
			int textColor = (hoverState == 2) ? 0xFF00FF00 : 0xFF00CC00;
			drawCenteredString(fondRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
		}
	}

}
