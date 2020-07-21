package net.blay09.mods.chattweaks.gui.widget;

import net.blay09.mods.chattweaks.gui.FormattedFontRenderer;
import net.blay09.mods.chattweaks.gui.formatter.IStringFormatter;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

public class PasswordTextFieldWidget extends FormattedTextFieldWidget {

	public PasswordTextFieldWidget(Minecraft mc, int x, int y, int width, int height) {
		super(new FormattedFontRenderer(mc, mc.fontRenderer, new PasswordStringFormatter()), x, y, width, height);
	}

}
