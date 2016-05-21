package net.blay09.mods.bmc.api;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public interface IntegrationModule {
	String getModId();
	String getName();
	TextureAtlasSprite getIcon();
	GuiScreen getConfigScreen(GuiScreen parentScreen);
}
