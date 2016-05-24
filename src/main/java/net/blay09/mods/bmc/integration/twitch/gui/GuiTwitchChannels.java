package net.blay09.mods.bmc.integration.twitch.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.gui.GuiButtonLink;
import net.blay09.mods.bmc.gui.GuiOptionGroup;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.gui.settings.GuiButtonDeleteChannel;
import net.blay09.mods.bmc.gui.settings.GuiButtonDeleteChannelConfirm;
import net.blay09.mods.bmc.integration.twitch.TwitchChannel;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;

public class GuiTwitchChannels extends GuiScreenBase {

	private final List<GuiTwitchChannelCheckbox> channelButtons = Lists.newArrayList();

	private GuiButton btnServerSettings;
	private GuiCheckBox chkActive;
	private GuiCheckBox chkSubscribersOnly;
	private GuiCheckBox chkDeletedMessagesShow;
	private GuiCheckBox chkDeletedMessagesStrikethrough;
	private GuiCheckBox chkDeletedMessagesReplace;
	private GuiCheckBox chkDeletedMessagesHide;
	private GuiOptionGroup groupDeletedMessages;
	private GuiButtonDeleteChannel btnDeleteChannel;
	private GuiButtonDeleteChannelConfirm btnDeleteChannelConfirm;

	private GuiButton btnNewChannel;
	private GuiTextField txtNewChannelName;

	private TwitchChannel selectedChannel;

	public GuiTwitchChannels() {
		xSize = 250;
		ySize = 200;
		selectedChannel = TwitchIntegration.getTwitchChannels().iterator().next();
	}

	@Override
	public void initGui() {
		super.initGui();

		btnServerSettings = new GuiButton(0, width / 2 + 25, height / 2 - 94, 90, 19, "Twitch Settings");
		buttonList.add(btnServerSettings);

		chkActive = new GuiCheckBox(1, width / 2 - 12, height / 2 - 65, " is Active", false);
		buttonList.add(chkActive);

		chkSubscribersOnly = new GuiCheckBox(2, width / 2 - 12, height / 2 - 45, " Subscribers only", false);
		buttonList.add(chkSubscribersOnly);

		chkDeletedMessagesShow = new GuiCheckBox(3, width / 2 - 2, height / 2 - 10, " Show normally", false);
		buttonList.add(chkDeletedMessagesShow);

		chkDeletedMessagesStrikethrough = new GuiCheckBox(4, width / 2 - 2, height / 2 + 5, TextFormatting.STRIKETHROUGH + " Strikethrough", false);
		buttonList.add(chkDeletedMessagesStrikethrough);

		chkDeletedMessagesReplace = new GuiCheckBox(5, width / 2 - 2, height / 2 + 20, TextFormatting.ITALIC + " <message deleted>", false);
		buttonList.add(chkDeletedMessagesReplace);

		chkDeletedMessagesHide = new GuiCheckBox(6, width / 2 - 2, height / 2 + 35, " Remove completely", false);
		buttonList.add(chkDeletedMessagesHide);

		groupDeletedMessages = new GuiOptionGroup(chkDeletedMessagesHide, chkDeletedMessagesReplace, chkDeletedMessagesStrikethrough, chkDeletedMessagesShow);

		btnDeleteChannel = new GuiButtonDeleteChannel(7, width / 2 - 10, height / 2 + 70);
		buttonList.add(btnDeleteChannel);

		btnDeleteChannelConfirm = new GuiButtonDeleteChannelConfirm(8, width / 2 + 10, height / 2 + 75, Minecraft.getMinecraft().fontRendererObj);
		btnDeleteChannelConfirm.visible = false;
		buttonList.add(btnDeleteChannelConfirm);

		btnNewChannel = new GuiButtonLink(0, width / 2 - 97, 0, fontRendererObj, "<new>");
		buttonList.add(btnNewChannel);

		txtNewChannelName = new GuiTextField(0, fontRendererObj, width / 2 - 97, 10, 50, 10);
		textFieldList.add(txtNewChannelName);

		updateChannelList();

		setSelectedChannel(selectedChannel);

		addNavigationBar();
	}

	@Override
	protected void actionPerformed(@Nullable GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button instanceof GuiTwitchChannelCheckbox) {
			if (((GuiTwitchChannelCheckbox) button).getChannel() != selectedChannel) {
				apply();
				setSelectedChannel(((GuiTwitchChannelCheckbox) button).getChannel());
			}
			chkActive.setIsChecked(((GuiTwitchChannelCheckbox) button).isChecked());
		} else if(button == btnDeleteChannel) {
			btnDeleteChannelConfirm.visible = true;
		} else if(button == btnDeleteChannelConfirm) {
			if(selectedChannel != null) {
				TwitchIntegration.removeTwitchChannel(selectedChannel);
				updateChannelList();
			}
			setSelectedChannel(TwitchIntegration.getTwitchChannels().iterator().next());
			btnDeleteChannelConfirm.visible = false;
		} else if(button == btnServerSettings) {
			mc.displayGuiScreen(new GuiTwitchSettings());
		} else if(button == btnNewChannel) {
			setSelectedChannel(null);
			txtNewChannelName.yPosition = btnNewChannel.yPosition;
			txtNewChannelName.setVisible(true);
			txtNewChannelName.setFocused(true);
			btnNewChannel.visible = false;
		}
		groupDeletedMessages.actionPerformed(button);
		apply();
	}

	public void updateChannelList() {
		buttonList.removeAll(channelButtons);
		int y = height / 2 - 65;
		for(TwitchChannel channel : TwitchIntegration.getTwitchChannels()) {
			GuiTwitchChannelCheckbox channelButton = new GuiTwitchChannelCheckbox(-1, width / 2 - 110, y, this, channel);
			buttonList.add(channelButton);
			channelButtons.add(channelButton);
			y += 15;
		}
		txtNewChannelName.setVisible(false);
		txtNewChannelName.setFocused(false);
		txtNewChannelName.setText("");
		btnNewChannel.yPosition = y + 2;
		btnNewChannel.visible = true;
	}

	@Override
	public void onLostFocus(GuiTextField lostFocus) {
		if(lostFocus == txtNewChannelName && txtNewChannelName.getVisible()) {
			setSelectedChannel(TwitchIntegration.getTwitchChannels().iterator().next());
			String channelName = txtNewChannelName.getText();
			if(channelName.charAt(0) == '#') {
				channelName = channelName.substring(1);
			}
			if(!channelName.isEmpty()) {
				TwitchChannel channel = TwitchIntegration.getTwitchChannel(channelName);
				if(channel == null) {
					channel = new TwitchChannel(channelName);
					channel.setActive(true);
					channel.setTargetChannel(BetterMinecraftChatAPI.getChatChannel("*", false));
					TwitchIntegration.addTwitchChannel(channel);
				}
				setSelectedChannel(channel);
			}
			updateChannelList();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawSimpleWindow();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1f, 1f, 1f, 1f);

		// Top Section
		drawRoundRect(width / 2 - 115, height / 2 - 92, width / 2 + 19, height / 2 - 77, 0xDDFFFFFF);
		drawCenteredString(fontRendererObj, "Twitch", width / 2 - 50, height / 2 - 88, 0xFFFFFF);

		// Channel List
		drawRoundRect(width / 2 - 115, height / 2 - 70, width / 2 - 25, height / 2 + 90, 0xDDFFFFFF);

		// Settings
		drawRoundRect(width / 2 - 17, height / 2 - 70, width / 2 + 114, height / 2 + 90, 0xDDFFFFFF);
		drawHorizontalLine(width / 2 - 10, width / 2 + 107, height / 2 - 50, 0xDDFFFFFF);
		drawString(fontRendererObj, "Deleted messages", width / 2 - 10, height / 2 - 25, 0xFFFFFF);
	}

	public void setSelectedChannel(@Nullable TwitchChannel channel) {
		this.selectedChannel = channel;
		boolean isEnabled = channel != null;
		chkActive.enabled = isEnabled;
		chkSubscribersOnly.enabled = isEnabled;
		chkDeletedMessagesShow.enabled = isEnabled;
		chkDeletedMessagesStrikethrough.enabled = isEnabled;
		chkDeletedMessagesReplace.enabled = isEnabled;
		chkDeletedMessagesHide.enabled = isEnabled;
		if(channel != null) {
			chkActive.setIsChecked(channel.isActive());
			chkSubscribersOnly.setIsChecked(channel.isSubscribersOnly());
			chkDeletedMessagesShow.setIsChecked(channel.getDeletedMessages() == TwitchChannel.DeletedMessages.SHOW);
			chkDeletedMessagesStrikethrough.setIsChecked(channel.getDeletedMessages() == TwitchChannel.DeletedMessages.STRIKETHROUGH);
			chkDeletedMessagesReplace.setIsChecked(channel.getDeletedMessages() == TwitchChannel.DeletedMessages.REPLACE);
			chkDeletedMessagesHide.setIsChecked(channel.getDeletedMessages() == TwitchChannel.DeletedMessages.HIDE);
		}
	}

	public void apply() {
		if(selectedChannel != null) {
			boolean oldActive = selectedChannel.isActive();
			selectedChannel.setActive(chkActive.isChecked());
			selectedChannel.setSubscribersOnly(chkSubscribersOnly.isChecked());
			if (chkDeletedMessagesShow.isChecked()) {
				selectedChannel.setDeletedMessages(TwitchChannel.DeletedMessages.SHOW);
			} else if (chkDeletedMessagesStrikethrough.isChecked()) {
				selectedChannel.setDeletedMessages(TwitchChannel.DeletedMessages.STRIKETHROUGH);
			} else if (chkDeletedMessagesReplace.isChecked()) {
				selectedChannel.setDeletedMessages(TwitchChannel.DeletedMessages.REPLACE);
			} else if (chkDeletedMessagesHide.isChecked()) {
				selectedChannel.setDeletedMessages(TwitchChannel.DeletedMessages.HIDE);
			}
			if (selectedChannel.isActive() != oldActive) {
				TwitchIntegration.updateChannelStates();
			}
		}
	}

	public TwitchChannel getSelectedChannel() {
		return selectedChannel;
	}

	@Override
	protected boolean isTwitchGui() {
		return true;
	}
}
