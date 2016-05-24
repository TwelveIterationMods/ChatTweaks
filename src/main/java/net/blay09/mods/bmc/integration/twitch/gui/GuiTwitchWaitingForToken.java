package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

public class GuiTwitchWaitingForToken extends GuiScreenBase {

	public GuiTwitchWaitingForToken(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(mc.fontRendererObj, "Status: " + TextFormatting.YELLOW + "Awaiting authorization...", width / 2, height / 2 - 20, 0xFFFFFF);
		drawCenteredString(mc.fontRendererObj, TextFormatting.GRAY + "Please switch to your web browser and follow the instructions.", width / 2, height / 2 + 10, 0xFFFFFF);
	}

}
