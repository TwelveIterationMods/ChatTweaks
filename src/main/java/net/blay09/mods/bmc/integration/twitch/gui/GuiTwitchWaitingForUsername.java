package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiTwitchWaitingForUsername extends GuiScreenBase {

	public GuiTwitchWaitingForUsername(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(mc.fontRendererObj, "Status: " + TextFormatting.YELLOW + "Requesting username...", width / 2, height / 2 - 20, 0xFFFFFF);
		drawCenteredString(mc.fontRendererObj, TextFormatting.GRAY + "Please wait a moment.", width / 2, height / 2 + 10, 0xFFFFFF);
	}

}
