package net.blay09.mods.bmc.integration.twitch;

import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.balyware.gui.GuiPasswordField;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiTwitchConnect extends GuiScreenBase {

	private static final ResourceLocation twitchLogo = new ResourceLocation(TwitchIntegration.MOD_ID, "twitch_logo.png");

	private GuiButton btnGetToken;
	private GuiPasswordField txtToken;
	private GuiButton btnConnect;

	public GuiTwitchConnect(GuiScreen parentScreen) {
		super(parentScreen);
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		btnGetToken = new GuiButton(0, width / 2 - 100, height / 2 - 25, 200, 20, TextFormatting.GREEN + "Generate Token");
		buttonList.add(btnGetToken);

		txtToken = new GuiPasswordField(1, mc, width / 2 - 100, height / 2 + 20, 200, 15);
		AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
		if(tokenPair != null) {
			txtToken.setText(tokenPair.getToken());
		}
		txtToken.setEnabled(!TwitchIntegration.useAnonymousLogin);
		textFieldList.add(txtToken);

		GuiCheckBox chkAnonymous = new GuiCheckBox(2, width / 2 - 100, height / 2 + 45, " Anonymous Login (read-only)", TwitchIntegration.useAnonymousLogin) {
			@Override
			public void setIsChecked(boolean isChecked) {
				super.setIsChecked(isChecked);
				txtToken.setEnabled(isChecked);
				TwitchIntegration.useAnonymousLogin = isChecked;
			}
		};
		buttonList.add(chkAnonymous);

		btnConnect = new GuiButton(3, width / 2, height / 2 + 65, 100, 20, "Connect");
		buttonList.add(btnConnect);
	}

	@Override
	public void actionPerformed(GuiButton button) {
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
				TwitchIntegration.connect();
			}
		} else if(button == btnGetToken) {
			mc.displayGuiScreen(new GuiTwitchOpenToken(this, 0));
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		if(result) {
			if(id == 0) {
				mc.displayGuiScreen(new GuiTwitchWaitingForToken(parentScreen));
				TwitchHelper.listenForToken(parentScreen, new Runnable() {
					@Override
					public void run() {
						mc.displayGuiScreen(new GuiTwitchConnect(parentScreen));
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
		drawString(mc.fontRendererObj, "Chat Token", width / 2 - 100, height / 2 + 5, 0xFFFFFF);
	}

}
