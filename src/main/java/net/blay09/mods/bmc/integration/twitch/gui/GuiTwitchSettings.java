package net.blay09.mods.bmc.integration.twitch.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.TokenPair;
import net.blay09.mods.bmc.balyware.gui.GuiUtils;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.gui.settings.GuiButtonHelp;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegrationConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
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
	private GuiButtonHelp btnFormatHelp;

	private String loggedInAs;

	public GuiTwitchSettings() {
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		btnChannels = new GuiButton(0, width / 2 + 25, height / 2 - 94, 90, 19, I18n.format(TwitchIntegration.MOD_ID + ":gui.settings.twitchChannels"));
		buttonList.add(btnChannels);

		btnAuthentication = new GuiButton(1, width / 2 - 110, height / 2 - 45, 110, 20, I18n.format(TwitchIntegration.MOD_ID + ":gui.settings.editAuthentication"));
		buttonList.add(btnAuthentication);

		chkShowWhispers = new GuiCheckBox(2, width / 2 - 105, height / 2 + 25, I18n.format(TwitchIntegration.MOD_ID + ":gui.settings.displayWhispers"), TwitchIntegrationConfig.showWhispers);
		buttonList.add(chkShowWhispers);

		txtFormatMessage = new GuiTextField(3, fontRendererObj, width / 2 - 105, height / 2, 100, 13);
		txtFormatMessage.setText(TwitchIntegration.getTwitchManager().isMultiMode() ? TwitchIntegrationConfig.multiMessageFormat : TwitchIntegrationConfig.singleMessageFormat);
		textFieldList.add(txtFormatMessage);

		txtFormatAction = new GuiTextField(4, fontRendererObj, width / 2 + 5, height / 2, 100, 13);
		txtFormatAction.setText(TwitchIntegration.getTwitchManager().isMultiMode() ? TwitchIntegrationConfig.multiActionFormat : TwitchIntegrationConfig.singleActionFormat);
		textFieldList.add(txtFormatAction);

		txtFormatWhisperMessage = new GuiTextField(5, fontRendererObj, width / 2 - 105, height / 2 + 60, 100, 13);
		txtFormatWhisperMessage.setEnabled(chkShowWhispers.isChecked());
		txtFormatWhisperMessage.setText(TwitchIntegrationConfig.whisperMessageFormat);
		textFieldList.add(txtFormatWhisperMessage);

		txtFormatWhisperAction = new GuiTextField(6, fontRendererObj, width / 2 + 5, height / 2 + 60, 100, 13);
		txtFormatWhisperAction.setText(TwitchIntegrationConfig.whisperActionFormat);
		txtFormatWhisperAction.setEnabled(chkShowWhispers.isChecked());
		textFieldList.add(txtFormatWhisperAction);

		btnFormatHelp = new GuiButtonHelp(7, width / 2 + 90, height / 2 - 20);
		buttonList.add(btnFormatHelp);

		addNavigationBar();

		TokenPair tokenPair = BetterMinecraftChatAPI.getAuthManager().getToken(TwitchIntegration.MOD_ID);
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
		super.onGuiClosed();
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
		drawString(fontRendererObj, TextFormatting.GRAY + I18n.format("twitchintegration:gui.settings.loggedInAs", TextFormatting.WHITE + loggedInAs), width / 2 - 105, height / 2 - 60, 0xFFFFFF);

		drawString(fontRendererObj, I18n.format("twitchintegration:gui.settings.messageFormat"), width / 2 - 105, height / 2 - 15, 0xFFFFFF);
		drawString(fontRendererObj, I18n.format("twitchintegration:gui.settings.actionFormat"), width / 2 + 5, height / 2 - 15, 0xFFFFFF);

		drawString(fontRendererObj, I18n.format("twitchintegration:gui.settings.whisperFormat"), width / 2 - 105, height / 2 + 45, 0xFFFFFF);
		drawString(fontRendererObj, I18n.format("twitchintegration:gui.settings.whisperAction"), width / 2 + 5, height / 2 + 45, 0xFFFFFF);

		if(btnFormatHelp.isMouseOver()) {
			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + I18n.format("twitchintegration:gui.settings.formatTokens"), " %u: " + I18n.format("twitchintegration:gui.settings.sender"), " %m: " + I18n.format("twitchintegration:gui.settings.message"), " %c: " + I18n.format("twitchintegration:gui.settings.channel"), " %r: " + I18n.format("twitchintegration:gui.settings.receiver")), mouseX, mouseY);
		}
	}

	private void apply() {
		TwitchIntegrationConfig.showWhispers = chkShowWhispers.isChecked();
		if(TwitchIntegration.getTwitchManager().isMultiMode()) {
			TwitchIntegrationConfig.multiMessageFormat = txtFormatMessage.getText();
			TwitchIntegrationConfig.multiActionFormat = txtFormatAction.getText();
		} else {
			TwitchIntegrationConfig.singleMessageFormat = txtFormatMessage.getText();
			TwitchIntegrationConfig.singleActionFormat = txtFormatAction.getText();
		}
		TwitchIntegrationConfig.whisperMessageFormat = txtFormatWhisperMessage.getText();
		TwitchIntegrationConfig.whisperActionFormat = txtFormatWhisperAction.getText();
		TwitchIntegrationConfig.save();
	}

	@Override
	public String getNavigationId() {
		return TwitchIntegration.MOD_ID;
	}

}
