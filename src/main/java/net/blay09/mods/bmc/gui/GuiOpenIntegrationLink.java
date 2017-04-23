package net.blay09.mods.bmc.gui;

import net.blay09.mods.bmc.ChatTweaks;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class GuiOpenIntegrationLink extends GuiConfirmOpenLink {

	public GuiOpenIntegrationLink(GuiYesNoCallback callback, String context, String requiredModule, int id) {
		super(callback, TextFormatting.YELLOW + requiredModule + "\n\n" + TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.confirm.optionalModuleInstall"), id, true);
		messageLine1 = I18n.format(ChatTweaks.MOD_ID + ":gui.confirm.optionalModuleRequired", context);
		disableSecurityWarning();
	}

}
