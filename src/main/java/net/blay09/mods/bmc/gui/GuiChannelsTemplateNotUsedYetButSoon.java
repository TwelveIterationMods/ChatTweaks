package net.blay09.mods.bmc.gui;

import net.blay09.mods.bmc.gui.settings.GuiButtonAddChannel;
import net.blay09.mods.bmc.gui.settings.GuiButtonDeleteChannel;
import net.blay09.mods.bmc.gui.settings.GuiButtonNavigate;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiChannelsTemplateNotUsedYetButSoon extends GuiScreenBase {

	private GuiButtonNavigate btnNextServer;
	private GuiButtonNavigate btnPrevServer;
	private GuiButtonAddChannel btnAddServer;

	private GuiButton btnServerSettings;

	public GuiChannelsTemplateNotUsedYetButSoon() {
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();

		btnPrevServer = new GuiButtonNavigate(0, width / 2 - 112, height / 2 - 90, false);
		buttonList.add(btnPrevServer);

		btnNextServer = new GuiButtonNavigate(1, width / 2 - 10, height / 2 - 90, true);
		buttonList.add(btnNextServer);

		btnAddServer = new GuiButtonAddChannel(2, width / 2 + 5, height / 2 - 90);
		buttonList.add(btnAddServer);

		buttonList.add(new GuiButton(3, width / 2 + 25, height / 2 - 94, 90, 19, "Server Settings"));

		int y = height / 2 - 65;
		buttonList.add(new GuiCheckBox(0, width / 2 - 110, y, TextFormatting.WHITE + "blay09", true));
		buttonList.add(new GuiCheckBox(1, width / 2 - 110, y + 15, TextFormatting.GRAY + "ZeekDaGeek", false));
		buttonList.add(new GuiCheckBox(1, width / 2 - 110, y + 30, TextFormatting.GRAY + "Slowpoke101", true));
		buttonList.add(new GuiCheckBox(1, width / 2 - 110, y + 45, TextFormatting.GRAY + "Gamerkitty_1", false));

		buttonList.add(new GuiCheckBox(5, width / 2 - 12, height / 2 - 65, " is Active", false));



		buttonList.add(new GuiCheckBox(5, width / 2 - 12, height / 2 - 45, " Subscribers only", false));
		buttonList.add(new GuiCheckBox(5, width / 2 - 2, height / 2 - 10, " Show normally", false));
		buttonList.add(new GuiCheckBox(5, width / 2 - 2, height / 2 + 5, TextFormatting.STRIKETHROUGH + " Strikethrough", false));
		buttonList.add(new GuiCheckBox(5, width / 2 - 2, height / 2 + 20, TextFormatting.ITALIC + " <message deleted>", false));
		buttonList.add(new GuiCheckBox(5, width / 2 - 2, height / 2 + 35, " Remove completely", false));

		buttonList.add(new GuiButtonDeleteChannel(0, width / 2 - 10, height / 2 + 70));
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

	@Override
	public String getNavigationId() {
		return "";
	}
}
