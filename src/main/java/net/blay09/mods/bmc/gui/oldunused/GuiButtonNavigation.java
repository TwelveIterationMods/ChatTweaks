package net.blay09.mods.bmc.gui.oldunused;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonNavigation extends GuiButton {

	private final ResourceLocation iconTexture;
	private final boolean isAvailable;
	private final String navigationId;

	public GuiButtonNavigation(int buttonId, int x, int y, ResourceLocation iconTexture, boolean isAvailable, String navigationId) {
		super(buttonId, x, y, 28, 28, "");
		this.iconTexture = iconTexture;
		this.isAvailable = isAvailable;
		this.navigationId = navigationId;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if(!visible) {
			return;
		}
		this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		mc.getTextureManager().bindTexture(iconTexture);
		if(isAvailable) {
			if(!enabled || hovered) {
				GlStateManager.color(1f, 1f, 1f, 1f);
			} else {
				GlStateManager.color(1f, 1f, 1f, 0.7f);
			}
		} else {
			if(hovered) {
				GlStateManager.color(1f, 1f, 1f, 0.7f);
			} else {
				GlStateManager.color(0.25f, 0.25f, 0.25f, 1f);
			}
		}
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		drawModalRectWithCustomSizedTexture(xPosition + (enabled && hovered ? 2 : 0), yPosition, 0, 0, width, height, 28, 28);
	}

	public boolean isAvailable() {
		return isAvailable;
	}

}
