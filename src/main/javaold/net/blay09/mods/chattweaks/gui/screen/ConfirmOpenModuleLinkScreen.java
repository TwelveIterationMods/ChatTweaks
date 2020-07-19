package net.blay09.mods.chattweaks.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screen.ConfirmOpenLinkScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ConfirmOpenModuleLinkScreen extends ConfirmOpenLinkScreen {

	private final String url;

	public ConfirmOpenModuleLinkScreen(BooleanConsumer callback, String context, String requiredModule, int id, String url) {
		super(callback, TextFormatting.YELLOW + requiredModule + "\n\n" + TextFormatting.GRAY + I18n.format("chattweaks:gui.confirm.optionalModuleInstall"), false);
		this.url = url;
		// TODO messageLine1 = I18n.format("chattweaks:gui.confirm.optionalModuleRequired", context);
	}

	@Override
	public void copyLinkToClipboard() {
		this.minecraft.keyboardListener.setClipboardString(url);
	}

}
