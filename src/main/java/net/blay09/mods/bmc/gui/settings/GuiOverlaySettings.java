package net.blay09.mods.bmc.gui.settings;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.MessageStyle;
import net.blay09.mods.bmc.balyware.BalyWare;
import net.blay09.mods.bmc.balyware.gui.GuiUtils;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.gui.chat.GuiButtonChannelTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

public class GuiOverlaySettings {

	private final GuiScreen parentScreen;
	private final int x;
	private final int y;
	private final int width = 250;
	private final int height = 200;
	private boolean shouldRefresh;

	private ChatChannel activeChannel;
	private List<GuiTextField> textFields = Lists.newArrayList();
	private List<GuiButton> buttons = Lists.newArrayList();
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
	private GuiButtonNavigateChannel btnNextChannel;
	private GuiButtonAddChannel btnAddChannel;
	private GuiButtonNavigateChannel btnPrevChannel;
	private GuiButtonAddChannelTab btnAddChannelTab;

	public GuiOverlaySettings(GuiScreen parentScreen) {
		this.parentScreen = parentScreen;
		this.x = parentScreen.width / 2 - width / 2;
		this.y = parentScreen.height / 2 - height / 2 - 20;
		this.activeChannel = BetterMinecraftChat.getChatHandler().getActiveChannel();
		refresh();
	}

	public void refresh() {
		clear();

		txtLabel = new GuiTextField(-1, parentScreen.mc.fontRendererObj, x + 12, y + 36, 90, 13);
		txtLabel.setText(activeChannel.getName());
		textFields.add(txtLabel);

		txtPattern = GuiRegExField.create(-1, parentScreen.mc.fontRendererObj, x + 12, y + 70, 200, 13);
		txtPattern.setText(activeChannel.getFilterPattern());
		textFields.add(txtPattern);

		btnRegExHelp = new GuiButtonHelp(-1, x + 216, y + 69);
		buttons.add(btnRegExHelp);

		txtPrefix = new GuiTextField(-1, parentScreen.mc.fontRendererObj, x + 122, y + 36, 90, 13);
		if(activeChannel.getOutgoingPrefix() != null) {
			txtPrefix.setText(activeChannel.getOutgoingPrefix());
		}
		textFields.add(txtPrefix);

		btnPrefixHelp = new GuiButtonHelp(-1, x + 216, y + 35);
		buttons.add(btnPrefixHelp);

		txtFormat = GuiFormatField.create(-1, parentScreen.mc.fontRendererObj, x + 12, y + 118, 140, 13);
		txtFormat.setText(activeChannel.getFormat());
		textFields.add(txtFormat);

		btnFormatHelp = new GuiButtonHelp(-1, x + 156, y + 117);
		buttons.add(btnFormatHelp);

		btnStyle = new GuiButtonStyle(-1, x + 175, y + 117);
		btnStyle.displayString = activeChannel.getMessageStyle().name();
		buttons.add(btnStyle);

		chkTimestamps = new GuiCheckBox(-1, x + 12, y + 140, " Show Timestamps", activeChannel.isShowTimestamp());
		buttons.add(chkTimestamps);

		chkMuted = new GuiCheckBox(-1, x + 12, y + 155, " Mute Notifications", activeChannel.isMuted());
		buttons.add(chkMuted);

		chkExclusive = new GuiCheckBox(-1, x + 170, y + 180, " Exclusive", activeChannel.isExclusive());
		buttons.add(chkExclusive);

		btnDeleteChannel = new GuiButtonDeleteChannel(-1, x + 4, y + 178);
		buttons.add(btnDeleteChannel);

		btnDeleteChannelConfirm = new GuiButtonDeleteChannelConfirm(-1, x + 24, y + 183, Minecraft.getMinecraft().fontRendererObj);
		btnDeleteChannelConfirm.visible = false;
		buttons.add(btnDeleteChannelConfirm);

		btnPrevChannel = new GuiButtonNavigateChannel(-1, x + 200, y + 4, false);
		buttons.add(btnPrevChannel);

		btnAddChannel = new GuiButtonAddChannel(-1, x + 215, y + 4);
		buttons.add(btnAddChannel);

		btnNextChannel = new GuiButtonNavigateChannel(-1, x + 230, y + 4, true);
		buttons.add(btnNextChannel);

		for(GuiButton button : buttons) {
			parentScreen.buttonList.add(button);
		}

		int buttonX = 2;
		for (GuiButton button : parentScreen.buttonList) {
			if(button instanceof GuiButtonChannelTab) {
				if(!((GuiButtonChannelTab) button).getChannel().isHidden()) {
					buttonX += button.width + 2;
				}
			}
		}
		btnAddChannelTab = new GuiButtonAddChannelTab(-1, buttonX, parentScreen.height - 25, Minecraft.getMinecraft().fontRendererObj);
		parentScreen.buttonList.add(btnAddChannelTab);
	}

	public void actionPerformed(GuiButton button) {
		if(button == chkMuted || button == chkTimestamps) {
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
				BetterMinecraftChat.getGuiChatHandler().updateChannelButtons(parentScreen);
				if(BetterMinecraftChat.getChatHandler().getActiveChannel() == activeChannel) {
					ChatChannel channel = BetterMinecraftChat.getChatHandler().getNextChatChannel(activeChannel);
					if(channel != null) {
						BetterMinecraftChat.getChatHandler().setActiveChannel(channel);
					}
				}
				shouldRefresh = true;
			}
		} else if(button == btnAddChannel || button == btnAddChannelTab) {
			ChatChannel channel = new ChatChannel("untitled");
			BetterMinecraftChat.getChatHandler().addChannel(channel);
			BetterMinecraftChat.getChatHandler().setActiveChannel(channel);
			BetterMinecraftChat.getGuiChatHandler().updateChannelButtons(parentScreen);
			selectChannel(channel);
		} else if(button == btnPrevChannel) {
			List<ChatChannel> channels = BetterMinecraftChat.getChatHandler().getChannels();
			int index = channels.indexOf(activeChannel);
			index--;
			if(index < 0) {
				index = channels.size() - 1;
			}
			selectChannel(channels.get(index));
		} else if(button == btnNextChannel) {
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
			BetterMinecraftChat.getGuiChatHandler().updateChannelButtons(parentScreen);
			refresh();
		}
	}

	public boolean mouseClicked(int mouseX, int mouseY, int button) {
		if(btnDeleteChannelConfirm.visible && !btnDeleteChannelConfirm.isMouseOver()) {
			btnDeleteChannelConfirm.visible = false;
		}
		boolean anyFocus = false;
		GuiTextField lostFocus = null;
		for(GuiTextField textField : textFields) {
			boolean oldFocused = textField.isFocused();
			textField.mouseClicked(mouseX, mouseY, button);
			if(textField.isFocused()) {
				((GuiChat) parentScreen).inputField.setFocused(false);
				anyFocus = true;
			}
			if(oldFocused && !textField.isFocused()) {
				lostFocus = textField;
			}
		}
		if(lostFocus != null) {
			onLostFocus(lostFocus);
		}
		if(!anyFocus) {
			((GuiChat) parentScreen).inputField.setFocused(true);
		}
		return false;
	}

	public boolean keyTyped(int keyCode, char unicode) {
		GuiTextField focus = null;
		for(GuiTextField textField : textFields) {
			if(textField.isFocused()) {
				focus = textField;
			}
			if(textField.textboxKeyTyped(unicode, keyCode)) {
				return true;
			}
		}
		if(focus != null && keyCode == Keyboard.KEY_RETURN) {
			focus.setFocused(false);
			onLostFocus(focus);
			((GuiChat) parentScreen).inputField.setFocused(true);
			return true;
		}
		if(keyCode == Keyboard.KEY_ESCAPE) {
			apply(true);
		}
		return false;
	}

	public void clear() {
		Iterator<GuiButton> it = parentScreen.buttonList.iterator();
		while(it.hasNext()) {
			GuiButton button = it.next();
			if(button instanceof GuiButtonAddChannelTab) {
				it.remove();
			}
		}
		for(GuiButton button : buttons) {
			parentScreen.buttonList.remove(button);
		}
		buttons.clear();
		textFields.clear();
	}

	private void onLostFocus(GuiTextField textField) {
		if(textField == txtLabel) {
			apply(true);
			BetterMinecraftChat.getGuiChatHandler().updateChannelButtons(parentScreen);
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

	public void drawOverlayBackground(int mouseX, int mouseY) {
		Gui.drawRect(x - 1, y - 1, x + width + 1, y + height + 1, 0xDDFFFFFF);
		Gui.drawRect(x, y, x + width, y + height, 0xFF000000);
		List<ChatChannel> channels = BetterMinecraftChat.getChatHandler().getChannels();
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Settings for [" + txtLabel.getText() + "] (" + (channels.indexOf(activeChannel) + 1) + "/" + channels.size() + ")", x + 4, y + 4, 0xFFFFFFFF);
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Tab Label", x + 8, y + 22, 0xFFFFFFFF);
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Filter Pattern", x + 8, y + 56, 0xFFFFFFFF);
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Outgoing Prefix", x + 118, y + 22, 0xFFFFFFFF);
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Message Format", x + 8, y + 104, 0xFFFFFFFF);
		parentScreen.mc.fontRendererObj.drawStringWithShadow("Style", x + 174, y + 104, 0xFFFFFFFF);
	}

	public void drawOverlay(int mouseX, int mouseY) {
		if(shouldRefresh) {
			refresh();
			shouldRefresh = false;
		}
		for(GuiTextField textField : textFields) {
			textField.drawTextBox();
		}
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
		apply(false);
		this.activeChannel = channel;
		shouldRefresh = true;
	}

	public ChatChannel getSelectedChannel() {
		return activeChannel;
	}
}
