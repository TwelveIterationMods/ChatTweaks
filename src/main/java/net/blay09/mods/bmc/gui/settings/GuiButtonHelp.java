package net.blay09.mods.bmc.gui.settings;

import net.blay09.mods.bmc.BetterMinecraftChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonHelp extends GuiButton {

	protected static final ResourceLocation texture = new ResourceLocation(BetterMinecraftChat.MOD_ID, "gui.png");

	public GuiButtonHelp(int buttonId, int x, int y) {
		super(buttonId, x, y, 16, 16, "?");
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			int hoverState = getHoverState(hovered);
			mc.getTextureManager().bindTexture(texture);
			GlStateManager.color(1f, 1f, 1f, 1f);
			Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, 48, hoverState == 2 ? 16 : (hoverState == 1 ? 0 : 32), 16, 16, 128, 64);
			int textColor = 0xE0E0E0;
			if (packedFGColour != 0) {
				textColor = packedFGColour;
			} else if (!enabled) {
				textColor = 0xA0A0A0;
			} else if (hovered) {
				textColor = 0xFFFFA0;
			}
			drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
		}
	}

}
