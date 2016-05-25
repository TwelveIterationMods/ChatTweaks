package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.balyware.gui.GuiPasswordField;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.integration.twitch.TwitchHelper;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import javax.annotation.Nullable;
import java.io.IOException;

public class GuiTwitchAuthentication extends GuiScreenBase {

	private static final ResourceLocation twitchLogo = new ResourceLocation(TwitchIntegration.MOD_ID, "twitch_logo.png");

	private GuiButton btnGetToken;
	private GuiPasswordField txtToken;
	private GuiButton btnConnect;

	public GuiTwitchAuthentication(GuiScreen parentScreen) {
		super(parentScreen);
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		btnGetToken = new GuiButton(0, width / 2 - 100, height / 2 - 25, 200, 20, TextFormatting.GREEN + I18n.format(TwitchIntegration.MOD_ID + ":gui.authentication.generateToken"));
		buttonList.add(btnGetToken);

		txtToken = new GuiPasswordField(1, mc, width / 2 - 100, height / 2 + 20, 200, 15);
		AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
		if(tokenPair != null) {
			txtToken.setText(tokenPair.getToken());
		}
		txtToken.setEnabled(!TwitchIntegration.useAnonymousLogin);
		textFieldList.add(txtToken);

		GuiCheckBox chkAnonymous = new GuiCheckBox(2, width / 2 - 100, height / 2 + 45, I18n.format(TwitchIntegration.MOD_ID + ":gui.authentication.anonymousLogin"), TwitchIntegration.useAnonymousLogin) {
			@Override
			public void setIsChecked(boolean isChecked) {
				super.setIsChecked(isChecked);
				txtToken.setEnabled(isChecked);
				TwitchIntegration.useAnonymousLogin = isChecked;
			}
		};
		buttonList.add(chkAnonymous);

		btnConnect = new GuiButton(3, width / 2, height / 2 + 65, 100, 20, I18n.format(TwitchIntegration.MOD_ID + ":gui.authentication.connect"));
		if(TwitchIntegration.isConnected()) {
			btnConnect.displayString = I18n.format(TwitchIntegration.MOD_ID + ":gui.authentication.disconnect");
		}
		buttonList.add(btnConnect);

		addNavigationBar();
	}

	@Override
	public void actionPerformed(@Nullable GuiButton button) throws IOException {
		if(button == btnConnect) {
			AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
			if(tokenPair == null || !tokenPair.getToken().equals(txtToken.getText()) || tokenPair.getUsername() == null) {
				mc.displayGuiScreen(new GuiTwitchWaitingForUsername(parentScreen));
				TwitchHelper.requestUsername(txtToken.getText(), new Runnable() {
					@Override
					public void run() {
						TwitchIntegration.connect();
					}
				});
			} else {
				mc.displayGuiScreen(null);
				if(TwitchIntegration.isConnected()) {
					TwitchIntegration.disconnect();
				} else {
					TwitchIntegration.connect();
				}
			}
		} else if(button == btnGetToken) {
			mc.displayGuiScreen(new GuiTwitchOpenToken(this, 0));
		} else {
			super.actionPerformed(button);
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		super.confirmClicked(result, id);
		if(result) {
			if(id == 0) {
				mc.displayGuiScreen(new GuiTwitchWaitingForToken(parentScreen));
				TwitchHelper.listenForToken(parentScreen, new Runnable() {
					@Override
					public void run() {
						mc.displayGuiScreen(new GuiTwitchAuthentication(parentScreen));
					}
				});
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawSimpleWindow();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1f, 1f, 1f, 1f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(twitchLogo);
		drawModalRectWithCustomSizedTexture(width / 2 - 64, height / 2 - 80, 0, 0, 128, 43, 128, 43);
		drawString(mc.fontRendererObj, I18n.format(TwitchIntegration.MOD_ID + ":gui.authentication.chatToken"), width / 2 - 100, height / 2 + 5, 0xFFFFFF);
	}

	@Override
	public String getNavigationId() {
		return TwitchIntegration.MOD_ID;
	}
}
