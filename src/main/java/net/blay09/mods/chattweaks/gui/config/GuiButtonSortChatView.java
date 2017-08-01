package net.blay09.mods.chattweaks.gui.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSortChatView extends GuiButton {

	private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
	private final GuiChatViewsConfig.ChatViewArrayEntry parent;
	private final int sortDir;

	public GuiButtonSortChatView(int id, int x, int y, GuiChatViewsConfig.ChatViewArrayEntry parent, int sortDir) {
		super(id, x, y, "");
		this.width = 11;
		this.height = 7;
		this.parent = parent;
		this.sortDir = sortDir;
	}

	public GuiChatViewsConfig.ChatViewArrayEntry getParent() {
		return parent;
	}

	public int getSortDir() {
		return sortDir;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		drawButton(mc, mouseX, mouseY, 0, 0, 0, 0);
	}

	public void drawButton(Minecraft mc, int mouseX, int mouseY, int slotX, int slotY, int listWidth, int slotHeight) {
		GlStateManager.color(1f, 1f, 1f, 1f);
		mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
		if (visible && mouseY >= slotY && mouseY < slotY + slotHeight) {
			xPosition = listWidth / 4 - 2;
			yPosition = slotY + 1;
			if(sortDir == 1) {
				yPosition += 8;
			}
			hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			if (hovered) {
				Gui.drawModalRectWithCustomSizedTexture(xPosition - 3, yPosition - 5 + (sortDir == 1 ? -15 : 0), 96f - (sortDir == 1 ? 32f : 0f), 32f, 32, 32, 256f, 256f);
			} else {
				Gui.drawModalRectWithCustomSizedTexture(xPosition - 3, yPosition - 5 + (sortDir == 1 ? -15 : 0), 96f - (sortDir == 1 ? 32f : 0f), 0f, 32, 32, 256f, 256f);
			}
		}
	}
}
