package net.blay09.mods.bmc.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public interface IGuiOverlay {

	void initGui();
	void actionPerformed(GuiButton button);
	void mouseScrolled(int delta);
	boolean mouseClicked(int mouseX, int mouseY, int button);
	boolean keyTyped(int keyCode, char unicode);
	void drawOverlayBackground(int mouseX, int mouseY);
	void drawOverlay(int mouseX, int mouseY);
	void clear();
	void onGuiClosed();
	IGuiOverlay recreateFor(IGuiOverlay overlay, GuiScreen guiScreen);
}
