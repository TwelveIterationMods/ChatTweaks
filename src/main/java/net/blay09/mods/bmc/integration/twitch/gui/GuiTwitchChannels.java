package net.blay09.mods.bmc.integration.twitch.gui;

import jdk.nashorn.internal.runtime.regexp.joni.constants.TargetInfo;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.gui.settings.GuiButtonAddChannel;
import net.blay09.mods.bmc.gui.settings.GuiButtonDeleteChannel;
import net.blay09.mods.bmc.gui.settings.GuiButtonDeleteChannelConfirm;
import net.blay09.mods.bmc.gui.settings.GuiButtonNavigate;
import net.blay09.mods.bmc.integration.twitch.TwitchChannel;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

import java.io.IOException;

public class GuiTwitchChannels extends GuiScreenBase {

	private GuiButton btnServerSettings;

	private GuiCheckBox chkActive;
	private GuiCheckBox chkSubscribersOnly;
	private GuiCheckBox chkDeletedMessagesShow;
	private GuiCheckBox chkDeletedMessagesStrikethrough;
	private GuiCheckBox chkDeletedMessagesReplace;
	private GuiCheckBox chkDeletedMessagesHide;
	private GuiButtonDeleteChannel btnDeleteChannel;
	private GuiButtonDeleteChannelConfirm btnDeleteChannelConfirm;

	private TwitchChannel selectedChannel;

	public GuiTwitchChannels() {
		xSize = 250;
		ySize = 200;
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

		btnDeleteChannel = new GuiButtonDeleteChannel(7, width / 2 - 10, height / 2 + 70);
		buttonList.add(btnDeleteChannel);

		btnDeleteChannelConfirm = new GuiButtonDeleteChannelConfirm(8, width / 2 + 10, height / 2 + 80, Minecraft.getMinecraft().fontRendererObj);
		btnDeleteChannelConfirm.visible = false;
		buttonList.add(btnDeleteChannelConfirm);

		int y = height / 2 - 65;
		for(TwitchChannel channel : TwitchIntegration.getTwitchChannels()) {
			buttonList.add(new GuiTwitchChannelCheckbox(-1, width / 2 - 110, y, channel));
			y += 15;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if(button instanceof GuiTwitchChannelCheckbox) {
			((GuiTwitchChannelCheckbox) button).getChannel().setActive(((GuiTwitchChannelCheckbox) button).isChecked());
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawSimpleWindow();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.color(1f, 1f, 1f, 1f);

		// Top Section
		drawRoundRect(width / 2 - 115, height / 2 - 92, width / 2 + 19, height / 2 - 77, 0xDDFFFFFF);
		drawCenteredString(fontRendererObj, "Twitch", width / 2 - 55, height / 2 - 88, 0xFFFFFF);

		// Channel List
		drawRoundRect(width / 2 - 115, height / 2 - 70, width / 2 - 25, height / 2 + 90, 0xDDFFFFFF);

		// Settings
		drawRoundRect(width / 2 - 17, height / 2 - 70, width / 2 + 114, height / 2 + 90, 0xDDFFFFFF);
		drawHorizontalLine(width / 2 - 10, width / 2 + 107, height / 2 - 50, 0xDDFFFFFF);
		drawString(fontRendererObj, "Deleted messages", width / 2 - 10, height / 2 - 25, 0xFFFFFF);
	}

}
