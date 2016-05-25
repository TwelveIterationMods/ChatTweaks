package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiTwitchWaitingForUsername extends GuiScreenBase {

	public GuiTwitchWaitingForUsername(GuiScreen parentScreen) {
		super(parentScreen);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(mc.fontRendererObj, I18n.format(TwitchIntegration.MOD_ID + ":gui.awaitingResponse.status", TextFormatting.YELLOW + I18n.format(TwitchIntegration.MOD_ID + ":gui.awaitingResponse.requestingUsername")), width / 2, height / 2 - 20, 0xFFFFFF);
		drawCenteredString(mc.fontRendererObj, TextFormatting.GRAY + TwitchIntegration.MOD_ID + ":gui.awaitingResponse.pleaseWait", width / 2, height / 2 + 10, 0xFFFFFF);
	}

	@Override
	public String getNavigationId() {
		return TwitchIntegration.MOD_ID;
	}
}
