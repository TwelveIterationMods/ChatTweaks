package net.blay09.mods.chattweaks.gui;

import net.blay09.mods.chattweaks.gui.formatter.IStringFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

/**
 * WARNING
 * THIS CLASS IS QUITE SPECIFIC FOR THE FORMATTED TEXT BOX
 * FOR EXAMPLE IT WILL NOT FORMAT THE CURSOR ("_")
 */
public class FormattedFontRenderer extends FontRenderer {

	private static final ResourceLocation texture = new ResourceLocation("textures/font/ascii.png");

	private final FontRenderer baseFontRenderer;
	private final IStringFormatter formatter;

	private String lastText;
	private String lastFormattedText;
	private boolean isVisible;

	public FormattedFontRenderer(Minecraft mc, FontRenderer fontRenderer, IStringFormatter formatter) {
		super(mc.gameSettings, texture, mc.renderEngine, fontRenderer.getUnicodeFlag());
		setBidiFlag(fontRenderer.getBidiFlag());
		this.baseFontRenderer = fontRenderer;
		this.formatter = formatter;
		onResourceManagerReload(mc.getResourceManager());
	}

	@Override
	public int drawString(@Nullable String text, float x, float y, int color, boolean dropShadow) {
		if(!isVisible) {
			return 0;
		}
		if(text != null && !text.equals("_")) {
			if(!text.equals(lastText)) {
				lastFormattedText = formatter.applyFormatting(text);
				lastText = text;
			}
			text = lastFormattedText;
		}
		return super.drawString(text, x, y, color, dropShadow);
	}

	public FontRenderer getBaseFontRenderer() {
		return baseFontRenderer;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}
}
