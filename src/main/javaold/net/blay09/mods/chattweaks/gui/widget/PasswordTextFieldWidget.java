package net.blay09.mods.chattweaks.gui.widget;

import net.blay09.mods.chattweaks.gui.FormattedFontRenderer;
import net.blay09.mods.chattweaks.gui.formatter.IStringFormatter;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

public class PasswordTextFieldWidget extends FormattedTextFieldWidget {

	private static class PasswordFormatter implements IStringFormatter {
		@Override
		public String applyFormatting(String input) {
			return StringUtils.repeat('*', input.length());
		}
	}

	public PasswordTextFieldWidget(Minecraft mc, int x, int y, int width, int height) {
		super(new FormattedFontRenderer(mc, mc.fontRenderer, new PasswordFormatter()), x, y, width, height);
	}

}
