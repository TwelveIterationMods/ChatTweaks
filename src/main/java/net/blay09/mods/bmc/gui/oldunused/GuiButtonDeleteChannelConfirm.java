package net.blay09.mods.bmc.gui.oldunused;

import net.blay09.mods.bmc.ChatTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class  GuiButtonDeleteChannelConfirm extends GuiButton {

	public GuiButtonDeleteChannelConfirm(int buttonId, int x, int y, FontRenderer fontRenderer) {
		super(buttonId, x, y, fontRenderer.getStringWidth(I18n.format(ChatTweaks.MOD_ID + ":gui.button.deleteConfirm")), fontRenderer.FONT_HEIGHT, TextFormatting.UNDERLINE + I18n.format(ChatTweaks.MOD_ID + ":gui.button.deleteConfirm"));
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			int hoverState = getHoverState(hovered);
			mc.fontRenderer.drawStringWithShadow(displayString, xPosition, yPosition, hoverState == 2 ? 0xFFFF0000 : 0xFFBB0000);
		}
	}

}
