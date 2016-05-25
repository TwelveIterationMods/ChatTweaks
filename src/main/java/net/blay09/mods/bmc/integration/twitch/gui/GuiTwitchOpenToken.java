package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiTwitchOpenToken extends GuiConfirmOpenLink {
	public GuiTwitchOpenToken(GuiYesNoCallback callback, int i) {
		super(callback, I18n.format(TwitchIntegration.MOD_ID + ":gui.openToken.requiredPermissions") + "\n" + TextFormatting.GRAY + I18n.format(TwitchIntegration.MOD_ID + ":gui.openToken.logIntoChat") + "\n\n" + TextFormatting.RESET + I18n.format(TwitchIntegration.MOD_ID + ":gui.openToken.openedInBrowser"), i, true);
		messageLine1 = I18n.format(TwitchIntegration.MOD_ID + ":gui.openToken.authorize");
		disableSecurityWarning();
	}
}
