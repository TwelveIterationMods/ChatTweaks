package net.blay09.mods.chattweaks.gui;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiOpenIntegrationLink extends GuiConfirmOpenLink {

	private final String url;

	public GuiOpenIntegrationLink(GuiYesNoCallback callback, String context, String requiredModule, int id, String url) {
		super(callback, TextFormatting.YELLOW + requiredModule + "\n\n" + TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.confirm.optionalModuleInstall"), id, true);
		this.url = url;
		messageLine1 = I18n.format(ChatTweaks.MOD_ID + ":gui.confirm.optionalModuleRequired", context);
		disableSecurityWarning();
	}

	@Override
	public void copyLinkToClipboard() {
		setClipboardString(url);
	}

}
