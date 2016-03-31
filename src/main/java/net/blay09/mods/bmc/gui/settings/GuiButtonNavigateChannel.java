package net.blay09.mods.bmc.gui.settings;

import net.blay09.mods.bmc.BetterMinecraftChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonNavigateChannel extends GuiButton {

	protected static final ResourceLocation texture = new ResourceLocation(BetterMinecraftChat.MOD_ID, "gui.png");
	private final boolean isNext;

	public GuiButtonNavigateChannel(int buttonId, int x, int y, boolean isNext) {
		super(buttonId, x, y, 12, 12, "");
		this.isNext = isNext;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			int hoverState = getHoverState(hovered);
			mc.getTextureManager().bindTexture(texture);
			GlStateManager.color(1f, 1f, 1f, 1f);
			Gui.drawModalRectWithCustomSizedTexture(xPosition, yPosition, isNext ? 36 : 12, hoverState == 2 ? 28 : (hoverState == 1 ? 16 : 40), 12, 12, 128, 64);
		}
	}

}
