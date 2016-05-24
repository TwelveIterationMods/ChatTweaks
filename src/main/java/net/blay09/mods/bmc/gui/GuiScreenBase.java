package net.blay09.mods.bmc.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public abstract class GuiScreenBase extends GuiScreen {

	protected final GuiScreen parentScreen;
	protected final List<GuiTextField> textFieldList = Lists.newArrayList();

	protected int xSize;
	protected int ySize;
	protected int guiLeft;
	protected int guiTop;

	public GuiScreenBase() {
		this.parentScreen = null;
	}

	public GuiScreenBase(@Nullable GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		textFieldList.clear();
		super.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void initGui() {
		guiLeft = width / 2 - xSize / 2;
		guiTop = height / 2 - ySize / 2;
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		super.mouseClicked(mouseX, mouseY, button);
		GuiTextField lostFocus = null;
		for (GuiTextField textField : textFieldList) {
			boolean oldFocused = textField.isFocused();
			textField.mouseClicked(mouseX, mouseY, button);
			if (oldFocused && !textField.isFocused()) {
				lostFocus = textField;
			}
		}
		if (lostFocus != null) {
			onLostFocus(lostFocus);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		GuiTextField focus = null;
		for (GuiTextField textField : textFieldList) {
			if (textField.isFocused()) {
				focus = textField;
			}
			if (textField.textboxKeyTyped(typedChar, keyCode)) {
				return;
			}
		}
		if (focus != null && keyCode == Keyboard.KEY_RETURN) {
			focus.setFocused(false);
			onLostFocus(focus);
		}
	}

	public void onLostFocus(GuiTextField lostFocus) {
	}

	public void drawWindow() {
		drawRoundRect(guiLeft - 1, guiTop - 1, guiLeft + xSize, guiTop + ySize, 0xDDFFFFFF);
		drawBackground(guiLeft, guiTop, xSize, ySize);
	}

	public void drawSimpleWindow() {
		drawRoundRect(guiLeft - 1, guiTop - 1, guiLeft + xSize, guiTop + ySize, 0xDDFFFFFF);
		drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, 0xFF000000);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		for (GuiTextField textField : textFieldList) {
			textField.drawTextBox();
		}
	}

	public void drawBackground(int x, int y, int width, int height) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		mc.getTextureManager().bindTexture(OPTIONS_BACKGROUND);
		GlStateManager.color(1f, 1f, 1f, 1f);
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexBuffer.pos(x, y + height, 0).tex(0, (float) height / 32f).color(64, 64, 64, 255).endVertex();
		vertexBuffer.pos(x + width, y + height, 0).tex((float) width / 32f, (float) height / 32f).color(64, 64, 64, 255).endVertex();
		vertexBuffer.pos(x + width, y, 0).tex((float) width / 32f, 0).color(64, 64, 64, 255).endVertex();
		vertexBuffer.pos(x, y, 0).tex(0, 0).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}

	public void drawRoundRect(int left, int top, int right, int bottom, int color) {
		drawHorizontalLine(left + 1, right - 1, top, color);
		drawHorizontalLine(left + 1, right - 1, bottom, color);
		drawVerticalLine(left, top, bottom, color);
		drawVerticalLine(right, top, bottom, color);
	}
}
