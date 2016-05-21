package net.blay09.mods.bmc.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.IGuiOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import java.util.List;

public abstract class GuiOverlay extends Gui implements IGuiOverlay {

	protected final Minecraft mc;
	protected final GuiScreen parentScreen;
	protected int x;
	protected int y;
	protected int width = 250;
	protected int height = 200;
	private List<GuiTextField> textFields = Lists.newArrayList();
	private List<GuiButton> buttons = Lists.newArrayList();

	public GuiOverlay(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		this.mc = parentScreen.mc;
		this.x = parentScreen.width / 2 - width / 2;
		this.y = parentScreen.height / 2 - height / 2;
	}

	@Override
	public void initGui() {
		clear();
	}

	protected void addTextField(GuiTextField textField) {
		textFields.add(textField);
	}

	protected void addButton(GuiButton button) {
		buttons.add(button);
		parentScreen.buttonList.add(button);
	}

	@Override
	public void clear() {
		for(GuiButton button : buttons) {
			parentScreen.buttonList.remove(button);
		}
		buttons.clear();
		textFields.clear();
	}

	@Override
	public void actionPerformed(GuiButton button) {

	}

	@Override
	public void mouseScrolled(int delta) {

	}

	@Override
	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		boolean anyFocus = false;
		GuiTextField lostFocus = null;
		for(GuiTextField textField : textFields) {
			boolean oldFocused = textField.isFocused();
			textField.mouseClicked(mouseX, mouseY, button);
			if(textField.isFocused()) {
				((GuiChat) parentScreen).inputField.setFocused(false);
				anyFocus = true;
			}
			if(oldFocused && !textField.isFocused()) {
				lostFocus = textField;
			}
		}
		if(lostFocus != null) {
			onLostFocus(lostFocus);
		}
		if(!anyFocus) {
			((GuiChat) parentScreen).inputField.setFocused(true);
		}
		return false;
	}

	public void onLostFocus(GuiTextField lostFocus) {

	}

	@Override
	public boolean keyTyped(int keyCode, char unicode) {
		GuiTextField focus = null;
		for(GuiTextField textField : textFields) {
			if(textField.isFocused()) {
				focus = textField;
			}
			if(textField.textboxKeyTyped(unicode, keyCode)) {
				return true;
			}
		}
		if(focus != null && keyCode == Keyboard.KEY_RETURN) {
			focus.setFocused(false);
			onLostFocus(focus);
			((GuiChat) parentScreen).inputField.setFocused(true);
			return true;
		}
		return false;
	}

	public void drawOverlayBackground(int mouseX, int mouseY) {
		Gui.drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xDDFFFFFF);
		Gui.drawRect(x, y, x + width, y + height, 0xFF000000);
	}

	public void drawOverlay(int mouseX, int mouseY) {
		for(GuiTextField textField : textFields) {
			textField.drawTextBox();
		}
	}

	@Override
	public void onGuiClosed() {
		clear();
	}

}
