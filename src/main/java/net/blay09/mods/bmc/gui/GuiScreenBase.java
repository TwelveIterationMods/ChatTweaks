package net.blay09.mods.bmc.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.INavigationGui;
import net.blay09.mods.bmc.balyware.BalyWare;
import net.blay09.mods.bmc.gui.settings.GuiTabSettings;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchChannels;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public abstract class GuiScreenBase extends GuiScreen implements INavigationGui {

	protected final GuiScreen parentScreen;
	protected final List<GuiTextField> textFieldList = Lists.newArrayList();

	protected int xSize;
	protected int ySize;
	protected int guiLeft;
	protected int guiTop;

	private GuiButtonNavigation btnSettings;
	private GuiButtonNavigation btnTwitchIntegration;

	private String clickedLink;

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

	@Override
	protected void actionPerformed(@Nullable GuiButton button) throws IOException {
		if (button == btnTwitchIntegration) {
			if (btnTwitchIntegration.isAvailable()) {
				AuthManager.TokenPair tokenPair = AuthManager.getToken(BetterMinecraftChat.TWITCH_INTEGRATION);
				if (tokenPair != null) {
					mc.displayGuiScreen(new GuiTwitchChannels());
				} else {
					mc.displayGuiScreen(new GuiTwitchAuthentication(this));
				}
			} else {
				clickedLink = "http://minecraft.curseforge.com/projects/betterminecraftchat";
				mc.displayGuiScreen(new GuiOpenIntegrationLink(this, "Twitch", "BetterMinecraftChat - Twitch Integration", 7777));
			}
		} else if(button == btnSettings) {
			mc.displayGuiScreen(new GuiTabSettings(null));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		super.confirmClicked(result, id);
		if(id == 7777 && result) {
			try {
				BalyWare.openWebLink(new URI(clickedLink));
			} catch (URISyntaxException ignored) {}
		}
		mc.displayGuiScreen(this);
	}

	public void addNavigationBar() {
		btnSettings = new GuiButtonNavigation(-1, guiLeft - 32, guiTop, new ResourceLocation(BetterMinecraftChat.MOD_ID, "icons/settings.png"), true, "settings");
		if(getNavigationId().equals("settings")) {
			btnSettings.xPosition += 2;
			btnSettings.enabled = false;
		}
		buttonList.add(btnSettings);

		btnTwitchIntegration = new GuiButtonNavigation(-1, guiLeft - 32, guiTop + 30, new ResourceLocation(BetterMinecraftChat.MOD_ID, "icons/twitch.png"), Loader.isModLoaded(BetterMinecraftChat.TWITCH_INTEGRATION), BetterMinecraftChat.TWITCH_INTEGRATION);
		if(getNavigationId().equals(BetterMinecraftChat.TWITCH_INTEGRATION)) {
			btnTwitchIntegration.xPosition += 2;
			btnTwitchIntegration.enabled = false;
		}
		buttonList.add(btnTwitchIntegration);
	}
}
