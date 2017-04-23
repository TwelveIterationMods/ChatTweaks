package net.blay09.mods.bmc.gui.oldunused;

import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.balyware.gui.FormattedFontRenderer;
import net.blay09.mods.bmc.balyware.gui.GuiFormattedTextField;
import net.blay09.mods.bmc.gui.settings.FormatStringFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiFormatField extends GuiFormattedTextField {

	public static GuiFormatField create(int id, FontRenderer fontRenderer, int x, int y, int width, int height) {
		return new GuiFormatField(id, new FormattedFontRenderer(Minecraft.getMinecraft(), fontRenderer, new FormatStringFormatter()), x, y, width, height);
	}

	public GuiFormatField(int id, FormattedFontRenderer fontRenderer, int x, int y, int width, int height) {
		super(id, fontRenderer, x, y, width, height);
		setDisplayTextWhenEmpty(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.textfield.originalFormat"));
		setEmptyText("$0");
	}

}
