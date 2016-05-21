package net.blay09.mods.bmc.integration.twitch;

import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.text.TextFormatting;

public class GuiTwitchOpenToken extends GuiConfirmOpenLink {
	public GuiTwitchOpenToken(GuiYesNoCallback callback, int i) {
		super(callback, "Required Permissions:\n" + TextFormatting.GRAY + "Log into chat and send messages\n\n" + TextFormatting.RESET + "The Authentication page will be opened in your browser.", i, true);
		messageLine1 = "Authorize Twitch Integration to use your account?";
		disableSecurityWarning();
	}
}
