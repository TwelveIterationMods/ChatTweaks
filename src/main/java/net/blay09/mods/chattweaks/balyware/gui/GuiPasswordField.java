package net.blay09.mods.chattweaks.balyware.gui;

import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

public class GuiPasswordField extends GuiFormattedTextField {

	private static class PasswordFormatter implements IStringFormatter {
		@Override
		public String applyFormatting(String input) {
			return StringUtils.repeat('*', input.length());
		}
	}

	public GuiPasswordField(int id, Minecraft mc, int x, int y, int width, int height) {
		super(id, new FormattedFontRenderer(mc, mc.fontRenderer, new PasswordFormatter()), x, y, width, height);
	}

}