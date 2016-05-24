package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import javax.annotation.Nullable;
import java.io.IOException;

public class GuiTwitchSettings extends GuiScreenBase {

	private GuiButton btnChannels;
	private GuiButton btnAuthentication;
	private GuiCheckBox chkShowWhispers;
	private GuiTextField txtFormatMessage;
	private GuiTextField txtFormatAction;
	private GuiTextField txtFormatWhisperMessage;
	private GuiTextField txtFormatWhisperAction;

	private String loggedInAs;

	public GuiTwitchSettings() {
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		btnChannels = new GuiButton(0, width / 2 + 25, height / 2 - 94, 90, 19, "Twitch Channels");
		buttonList.add(btnChannels);

		btnAuthentication = new GuiButton(1, width / 2 - 110, height / 2 - 45, 110, 20, "Edit Authentication");
		buttonList.add(btnAuthentication);

		chkShowWhispers = new GuiCheckBox(2, width / 2 - 105, height / 2 + 25, "Display Whispers in Chat", TwitchIntegration.showWhispers);
		buttonList.add(chkShowWhispers);

		txtFormatMessage = new GuiTextField(3, fontRendererObj, width / 2 - 105, height / 2, 100, 13);
		txtFormatMessage.setText(TwitchIntegration.isMultiMode() ? TwitchIntegration.multiMessageFormat : TwitchIntegration.singleMessageFormat);
		textFieldList.add(txtFormatMessage);

		txtFormatAction = new GuiTextField(4, fontRendererObj, width / 2 + 5, height / 2, 100, 13);
		txtFormatAction.setText(TwitchIntegration.isMultiMode() ? TwitchIntegration.multiActionFormat : TwitchIntegration.singleActionFormat);
		textFieldList.add(txtFormatAction);

		txtFormatWhisperMessage = new GuiTextField(3, fontRendererObj, width / 2 - 105, height / 2 + 60, 100, 13);
		txtFormatWhisperMessage.setEnabled(chkShowWhispers.isChecked());
		txtFormatWhisperMessage.setText(TwitchIntegration.whisperMessageFormat);
		textFieldList.add(txtFormatWhisperMessage);

		txtFormatWhisperAction = new GuiTextField(4, fontRendererObj, width / 2 + 5, height / 2 + 60, 100, 13);
		txtFormatWhisperAction.setText(TwitchIntegration.whisperActionFormat);
		txtFormatWhisperAction.setEnabled(chkShowWhispers.isChecked());
		textFieldList.add(txtFormatWhisperAction);

		addNavigationBar();

		AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
		if(tokenPair != null) {
			loggedInAs = tokenPair.getUsername();
		}
	}

	@Override
	protected void actionPerformed(@Nullable GuiButton button) throws IOException {
		if(button == btnAuthentication) {
			mc.displayGuiScreen(new GuiTwitchAuthentication(this));
		} else if(button == btnChannels) {
			mc.displayGuiScreen(new GuiTwitchChannels());
		} else if(button == chkShowWhispers) {
			txtFormatWhisperMessage.setEnabled(chkShowWhispers.isChecked());
			txtFormatWhisperAction.setEnabled(chkShowWhispers.isChecked());
			apply();
		} else {
			super.actionPerformed(button);
		}
	}

	@Override
	public void onGuiClosed() {
		apply();
	}

	@Override
	public void onLostFocus(GuiTextField lostFocus) {
		apply();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawSimpleWindow();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1f, 1f, 1f, 1f);

		// Top Section
		drawRoundRect(width / 2 - 115, height / 2 - 92, width / 2 + 19, height / 2 - 77, 0xDDFFFFFF);
		drawCenteredString(fontRendererObj, "Twitch", width / 2 - 50, height / 2 - 88, 0xFFFFFF);

		// Main Section
		drawRoundRect(width / 2 - 115, height / 2 - 70, width / 2 + 114, height / 2 + 90, 0xDDFFFFFF);
		drawString(fontRendererObj, TextFormatting.GRAY + "Logged in as " + TextFormatting.WHITE + loggedInAs, width / 2 - 105, height / 2 - 60, 0xFFFFFF);

		drawString(fontRendererObj, "Message Format", width / 2 - 105, height / 2 - 15, 0xFFFFFF);
		drawString(fontRendererObj, "Action Format", width / 2 + 5, height / 2 - 15, 0xFFFFFF);

		drawString(fontRendererObj, "Whisper Format", width / 2 - 105, height / 2 + 45, 0xFFFFFF);
		drawString(fontRendererObj, "Whisper Action", width / 2 + 5, height / 2 + 45, 0xFFFFFF);
	}

	@Override
	protected boolean isTwitchGui() {
		return true;
	}

	private void apply() {
		TwitchIntegration.showWhispers = chkShowWhispers.isChecked();
		if(TwitchIntegration.isMultiMode()) {
			TwitchIntegration.multiMessageFormat = txtFormatMessage.getText();
			TwitchIntegration.multiActionFormat = txtFormatAction.getText();
		} else {
			TwitchIntegration.singleMessageFormat = txtFormatMessage.getText();
			TwitchIntegration.singleActionFormat = txtFormatAction.getText();
		}
		TwitchIntegration.whisperMessageFormat = txtFormatWhisperMessage.getText();
		TwitchIntegration.whisperActionFormat = txtFormatWhisperAction.getText();
	}
}
