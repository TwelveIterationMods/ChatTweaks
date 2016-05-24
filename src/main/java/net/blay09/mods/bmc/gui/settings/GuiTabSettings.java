package net.blay09.mods.bmc.gui.settings;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.MessageStyle;
import net.blay09.mods.bmc.balyware.BalyWare;
import net.blay09.mods.bmc.balyware.gui.GuiUtils;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.gui.GuiButtonIntegration;
import net.blay09.mods.bmc.gui.GuiOpenIntegrationLink;
import net.blay09.mods.bmc.gui.GuiScreenBase;
import net.blay09.mods.bmc.gui.chat.GuiButtonChannelTab;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchChannels;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchConnect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class GuiTabSettings extends GuiScreenBase {

	private ChatChannel activeChannel;
	private GuiButton btnRegExHelp;
	private GuiButton btnFormatHelp;
	private GuiButton btnPrefixHelp;
	private GuiCheckBox chkTimestamps;
	private GuiCheckBox chkMuted;
	private GuiCheckBox chkExclusive;
	private GuiTextField txtLabel;
	private GuiTextField txtPattern;
	private GuiTextField txtFormat;
	private GuiTextField txtPrefix;
	private GuiButtonStyle btnStyle;
	private GuiButtonDeleteChannel btnDeleteChannel;
	private GuiButtonDeleteChannelConfirm btnDeleteChannelConfirm;
	private GuiButtonNavigate btnNextChannel;
	private GuiButtonAddChannel btnAddChannel;
	private GuiButtonNavigate btnPrevChannel;
	private GuiButtonIntegration btnTwitchIntegration;

	private String clickedLink;

	public GuiTabSettings(@Nullable GuiScreen parentScreen) {
		this(parentScreen, BetterMinecraftChat.getChatHandler().getActiveChannel());
	}

	public GuiTabSettings(@Nullable GuiScreen parentScreen, ChatChannel selectedChannel) {
		super(parentScreen);
		this.activeChannel = selectedChannel;
		xSize = 250;
		ySize = 200;
	}

	@Override
	public void initGui() {
		super.initGui();
		txtLabel = new GuiTextField(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 36, 90, 13);
		textFieldList.add(txtLabel);

		txtPattern = GuiRegExField.create(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 70, 200, 13);
		textFieldList.add(txtPattern);

		btnRegExHelp = new GuiButtonHelp(-1, guiLeft + 216, guiTop + 69);
		buttonList.add(btnRegExHelp);

		txtPrefix = new GuiTextField(-1, mc.fontRendererObj, guiLeft + 122, guiTop + 36, 90, 13);
		textFieldList.add(txtPrefix);

		btnPrefixHelp = new GuiButtonHelp(-1, guiLeft + 216, guiTop + 35);
		buttonList.add(btnPrefixHelp);

		txtFormat = GuiFormatField.create(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 118, 140, 13);
		textFieldList.add(txtFormat);

		btnFormatHelp = new GuiButtonHelp(-1, guiLeft + 156, guiTop + 117);
		buttonList.add(btnFormatHelp);

		btnStyle = new GuiButtonStyle(-1, guiLeft + 175, guiTop + 117);
		buttonList.add(btnStyle);

		chkTimestamps = new GuiCheckBox(-1, guiLeft + 12, guiTop + 140, I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.showTimestamps"), false);
		buttonList.add(chkTimestamps);

		chkMuted = new GuiCheckBox(-1, guiLeft + 12, guiTop + 155, I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.muted"), false);
		buttonList.add(chkMuted);

		chkExclusive = new GuiCheckBox(-1, guiLeft + 170, guiTop + 180, I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.exclusive"), false);
		buttonList.add(chkExclusive);

		btnDeleteChannel = new GuiButtonDeleteChannel(-1, guiLeft + 4, guiTop + 178);
		buttonList.add(btnDeleteChannel);

		btnDeleteChannelConfirm = new GuiButtonDeleteChannelConfirm(-1, guiLeft + 24, guiTop + 183, Minecraft.getMinecraft().fontRendererObj);
		btnDeleteChannelConfirm.visible = false;
		buttonList.add(btnDeleteChannelConfirm);

		btnPrevChannel = new GuiButtonNavigate(-1, guiLeft + 200, guiTop + 4, false);
		buttonList.add(btnPrevChannel);

		btnAddChannel = new GuiButtonAddChannel(-1, guiLeft + 215, guiTop + 4);
		buttonList.add(btnAddChannel);

		btnNextChannel = new GuiButtonNavigate(-1, guiLeft + 230, guiTop + 4, true);
		buttonList.add(btnNextChannel);

		selectChannel(activeChannel);


		btnTwitchIntegration = new GuiButtonIntegration(-1, guiLeft - 30, guiTop, new ResourceLocation(BetterMinecraftChat.MOD_ID, "addons/twitch.png"), Loader.isModLoaded(BetterMinecraftChat.TWITCH_INTEGRATION));
		buttonList.add(btnTwitchIntegration);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if(button instanceof GuiButtonChannelTab) {
			apply(false);
			selectChannel(((GuiButtonChannelTab) button).getChannel());
		} else if(button == chkMuted || button == chkTimestamps) {
			apply(true);
		} else if(button == btnRegExHelp) {
			try {
				BalyWare.openWebLink(new URI("http://regexr.com/"));
			} catch (URISyntaxException ignored) {}
		} else if(button == btnFormatHelp) {
			try {
				BalyWare.openWebLink(new URI("http://balyware.com/"));
			} catch (URISyntaxException ignored) {}
		} else if(button == btnStyle) {
			MessageStyle oldStyle = activeChannel.getMessageStyle();
			int id = oldStyle.ordinal();
			id++;
			if(id >= MessageStyle.values().length) {
				id = 0;
			}
			MessageStyle newStyle = MessageStyle.values()[id];
			activeChannel.setMessageStyle(newStyle);
			btnStyle.displayString = newStyle.name();
			if(oldStyle == MessageStyle.Chat || newStyle == MessageStyle.Chat) {
				if(BetterMinecraftChat.getChatHandler().getActiveChannel() == activeChannel) {
					ChatChannel channel = BetterMinecraftChat.getChatHandler().getNextChatChannel(activeChannel);
					if(channel != null) {
						BetterMinecraftChat.getChatHandler().setActiveChannel(channel);
					}
				}
			}
		} else if(button == btnAddChannel) {
			apply(false);
			ChatChannel channel = new ChatChannel(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.untitled"));
			BetterMinecraftChat.getChatHandler().addChannel(channel);
			BetterMinecraftChat.getChatHandler().setActiveChannel(channel);
			selectChannel(channel);
		} else if(button == btnPrevChannel) {
			apply(false);
			List<ChatChannel> channels = BetterMinecraftChat.getChatHandler().getChannels();
			int index = channels.indexOf(activeChannel);
			index--;
			if(index < 0) {
				index = channels.size() - 1;
			}
			selectChannel(channels.get(index));
		} else if(button == btnNextChannel) {
			apply(false);
			List<ChatChannel> channels = BetterMinecraftChat.getChatHandler().getChannels();
			int index = channels.indexOf(activeChannel);
			index++;
			if(index >= channels.size()) {
				index = 0;
			}
			selectChannel(channels.get(index));
		} else if(button == btnDeleteChannel && !btnDeleteChannelConfirm.visible) {
			btnDeleteChannelConfirm.visible = true;
		} else if(button == btnDeleteChannelConfirm) {
			BetterMinecraftChat.getChatHandler().removeChannel(activeChannel);
		} else if(button == btnTwitchIntegration) {
			if(btnTwitchIntegration.isAvailable()) {
				AuthManager.TokenPair tokenPair = AuthManager.getToken(BetterMinecraftChat.TWITCH_INTEGRATION);
				if(tokenPair != null) {
					mc.displayGuiScreen(new GuiTwitchChannels());
				} else {
					mc.displayGuiScreen(new GuiTwitchConnect(this));
				}
			} else {
				clickedLink = "http://minecraft.curseforge.com/projects/betterminecraftchat";
				mc.displayGuiScreen(new GuiOpenIntegrationLink(this, "Twitch", "BetterMinecraftChat - Twitch Integration", 0));
			}
		}
	}

	@Override
	public void confirmClicked(boolean result, int id) {
		super.confirmClicked(result, id);
		if(id == 0 && result) {
			try {
				BalyWare.openWebLink(new URI(clickedLink));
			} catch (URISyntaxException ignored) {}
		}
		mc.displayGuiScreen(this);
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
		super.mouseClicked(mouseX, mouseY, button);
		if(btnDeleteChannelConfirm.visible && !btnDeleteChannelConfirm.isMouseOver()) {
			btnDeleteChannelConfirm.visible = false;
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if(keyCode == Keyboard.KEY_ESCAPE) {
			apply(true);
		}
	}

	@Override
	public void onLostFocus(GuiTextField textField) {
		if(textField == txtLabel) {
			apply(true);
		} else if(textField == txtPattern || textField == txtFormat) {
			apply(true);
		} else if(textField == txtPrefix) {
			apply(false);
		}
	}

	public void apply(boolean refreshChat) {
		activeChannel.setName(txtLabel.getText());
		activeChannel.setFormat(txtFormat.getText());
		activeChannel.setFilterPattern(txtPattern.getText());
		activeChannel.setShowTimestamps(chkTimestamps.isChecked());
		activeChannel.setMuted(chkMuted.isChecked());
		activeChannel.setExclusive(chkExclusive.isChecked());
		BetterMinecraftChatConfig.saveChannels();
		if(refreshChat) {
			BetterMinecraftChat.getChatHandler().refreshChannel(activeChannel);
			if(BetterMinecraftChat.getChatHandler().getActiveChannel() == activeChannel) {
				BetterMinecraftChat.getChatHandler().setActiveChannel(activeChannel);
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawSimpleWindow();
		List<ChatChannel> channels = BetterMinecraftChat.getChatHandler().getChannels();
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.settingsFor", "[" + txtLabel.getText() + "] (" + (channels.indexOf(activeChannel) + 1) + "/" + channels.size() + ")"), guiLeft + 4, guiTop + 4, 0xFFFFFFFF);
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.tabLabel"), guiLeft + 8, guiTop + 22, 0xFFFFFFFF);
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.filterPattern"), guiLeft + 8, guiTop + 56, 0xFFFFFFFF);
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.outgoingPrefix"), guiLeft + 118, guiTop + 22, 0xFFFFFFFF);
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.messageFormat"), guiLeft + 8, guiTop + 104, 0xFFFFFFFF);
		mc.fontRendererObj.drawStringWithShadow(I18n.format(BetterMinecraftChat.MOD_ID + ":gui.tabSettings.style"), guiLeft + 174, guiTop + 104, 0xFFFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
		if(btnRegExHelp.isMouseOver()) {
			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + "Required Groups", " Sender: (?<s> ... )", " Message: (?<m> ... )", TextFormatting.YELLOW + "Open Help in Browser"), mouseX, mouseY);
		} else if(btnFormatHelp.isMouseOver()) {
			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + "Variables", " $0: Original Text", " ${s}: Sender", " ${m}: Message", " $...: Custom Groups", TextFormatting.YELLOW + "Open Help in Browser"), mouseX, mouseY);
		} else if(btnPrefixHelp.isMouseOver()) {
			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + "Message Prefix", "This will be put in front", "of messages you send.", "Commands work as well."), mouseX, mouseY);
		} else if(btnStyle.isMouseOver()) {
			switch(activeChannel.getMessageStyle()) {
				case Hidden:
					GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.AQUA + "Hidden", "Messages will not appear", "and the tab will be hidden.", TextFormatting.YELLOW + "Click to toggle"), mouseX, mouseY);
					break;
				case Chat:
					GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.AQUA + "Chat (Default)", "Messages will appear", "in Minecraft chat.", TextFormatting.YELLOW + "Click to toggle"), mouseX, mouseY);
					break;
				case Side:
					GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.AQUA + "Side Bar", "Messages will appear", "on the side and fade away.", TextFormatting.YELLOW + "Click to toggle"), mouseX, mouseY);
					break;
				case Bottom:
					GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.AQUA + "Bottom", "Messages will appear on the", "bottom and fade away.", "Only shows one message", "at a time.", TextFormatting.YELLOW + "Click to toggle"), mouseX, mouseY);
					break;
			}
		}
		GlStateManager.disableLighting();
	}

	public void selectChannel(ChatChannel channel) {
		this.activeChannel = channel;
		txtLabel.setText(activeChannel.getName());
		txtPattern.setText(activeChannel.getFilterPattern());
		if(activeChannel.getOutgoingPrefix() != null) {
			txtPrefix.setText(activeChannel.getOutgoingPrefix());
		}
		txtFormat.setText(activeChannel.getFormat());
		btnStyle.displayString = activeChannel.getMessageStyle().name();
		chkTimestamps.setIsChecked(activeChannel.isShowTimestamp());
		chkMuted.setIsChecked(activeChannel.isMuted());
		chkExclusive.setIsChecked(activeChannel.isExclusive());

	}

	public ChatChannel getSelectedChannel() {
		return activeChannel;
	}
}
