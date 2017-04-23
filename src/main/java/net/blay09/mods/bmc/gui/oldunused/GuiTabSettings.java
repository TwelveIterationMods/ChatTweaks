//package net.blay09.mods.bmc.gui.settings;
//
//import com.google.common.collect.Lists;
//import net.blay09.mods.bmc.ChatTweaks;
//import net.blay09.mods.bmc.ChatViewManager;
//import net.blay09.mods.bmc.gui.oldunused.INavigationGui;
//import net.blay09.mods.bmc.chat.MessageStyle;
//import net.blay09.mods.bmc.balyware.gui.GuiUtils;
//import net.blay09.mods.bmc.chat.ChatViewOld;
//import net.blay09.mods.bmc.gui.oldunused.GuiScreenBase;
//import net.blay09.mods.bmc.gui.chat.GuiButtonChatView;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.*;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.resources.I18n;
//import net.minecraft.util.text.TextFormatting;
//import net.minecraftforge.fml.client.config.GuiCheckBox;
//import org.lwjgl.input.Keyboard;
//
//import javax.annotation.Nullable;
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//public class GuiTabSettings extends GuiScreenBase implements INavigationGui {
//
////	private ChatViewOld activeChannel;
////	private GuiButton btnRegExHelp;
////	private GuiButton btnFormatHelp;
////	private GuiButton btnPrefixHelp;
////	private GuiCheckBox chkTimestamps;
////	private GuiCheckBox chkMuted;
////	private GuiCheckBox chkExclusive;
////	private GuiTextField txtLabel;
////	private GuiTextField txtPattern;
////	private GuiTextField txtFormat;
////	private GuiTextField txtPrefix;
////	private GuiButtonStyle btnStyle;
////	private GuiButtonDeleteChannel btnDeleteChannel;
////	private GuiButtonDeleteChannelConfirm btnDeleteChannelConfirm;
////	private GuiButtonNavigate btnNextChannel;
////	private GuiButtonAddChannel btnAddChannel;
////	private GuiButtonNavigate btnPrevChannel;
////
////	public GuiTabSettings(@Nullable GuiScreen parentScreen) {
////		this(parentScreen, ChatViewManager.getNextChatView(null));
////	}
////
////	public GuiTabSettings(@Nullable GuiScreen parentScreen, ChatViewOld selectedChannel) {
////		super(parentScreen);
////		this.activeChannel = selectedChannel;
////		xSize = 250;
////		ySize = 200;
////	}
////
////	@Override
////	public void initGui() {
////		super.initGui();
////		txtLabel = new GuiTextField(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 36, 90, 13);
////		textFieldList.add(txtLabel);
////
////		txtPattern = GuiRegExField.create(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 70, 200, 13);
////		textFieldList.add(txtPattern);
////
////		btnRegExHelp = new GuiButtonHelp(-1, guiLeft + 216, guiTop + 69);
////		buttonList.add(btnRegExHelp);
////
////		txtPrefix = new GuiTextField(-1, mc.fontRendererObj, guiLeft + 122, guiTop + 36, 90, 13);
////		textFieldList.add(txtPrefix);
////
////		btnPrefixHelp = new GuiButtonHelp(-1, guiLeft + 216, guiTop + 35);
////		buttonList.add(btnPrefixHelp);
////
////		txtFormat = GuiFormatField.create(-1, mc.fontRendererObj, guiLeft + 12, guiTop + 118, 140, 13);
////		textFieldList.add(txtFormat);
////
////		btnFormatHelp = new GuiButtonHelp(-1, guiLeft + 156, guiTop + 117);
////		buttonList.add(btnFormatHelp);
////
////		btnStyle = new GuiButtonStyle(-1, guiLeft + 175, guiTop + 117);
////		buttonList.add(btnStyle);
////
////		chkTimestamps = new GuiCheckBox(-1, guiLeft + 12, guiTop + 140, I18n.format(ChatTweaks.MOD_ID + ":gui.tabSettings.showTimestamps"), false);
////		buttonList.add(chkTimestamps);
////
////		chkMuted = new GuiCheckBox(-1, guiLeft + 12, guiTop + 155, I18n.format(ChatTweaks.MOD_ID + ":gui.tabSettings.muted"), false);
////		buttonList.add(chkMuted);
////
////		chkExclusive = new GuiCheckBox(-1, guiLeft + 170, guiTop + 180, I18n.format(ChatTweaks.MOD_ID + ":gui.tabSettings.exclusive"), false);
////		buttonList.add(chkExclusive);
////
////		btnDeleteChannel = new GuiButtonDeleteChannel(-1, guiLeft + 4, guiTop + 178);
////		buttonList.add(btnDeleteChannel);
////
////		btnDeleteChannelConfirm = new GuiButtonDeleteChannelConfirm(-1, guiLeft + 24, guiTop + 183, Minecraft.getMinecraft().fontRendererObj);
////		btnDeleteChannelConfirm.visible = false;
////		buttonList.add(btnDeleteChannelConfirm);
////
////		btnPrevChannel = new GuiButtonNavigate(-1, guiLeft + 200, guiTop + 4, false);
////		buttonList.add(btnPrevChannel);
////
////		btnAddChannel = new GuiButtonAddChannel(-1, guiLeft + 215, guiTop + 4);
////		buttonList.add(btnAddChannel);
////
////		btnNextChannel = new GuiButtonNavigate(-1, guiLeft + 230, guiTop + 4, true);
////		buttonList.add(btnNextChannel);
////
////		selectChannel(activeChannel);
////
////		addNavigationBar();
////	}
////
////	@Override
////	public void actionPerformed(@Nullable GuiButton button) throws IOException {
////		if(button instanceof GuiButtonChatView) {
////			apply(false);
////			selectChannel(((GuiButtonChatView) button).getView());
////		} else if(button == chkMuted || button == chkTimestamps) {
////			apply(true);
////		} else if(button == btnRegExHelp) {
//////			try {
//////				BalyWare.openWebLink(new URI("http://regexr.com/"));
//////			} catch (URISyntaxException ignored) {}
////		} else if(button == btnFormatHelp) {
//////			try {
//////				BalyWare.openWebLink(new URI("http://balyware.com/"));
//////			} catch (URISyntaxException ignored) {}
////		} else if(button == btnStyle) {
////			MessageStyle oldStyle = activeChannel.getMessageStyle();
////			int id = oldStyle.ordinal();
////			id++;
////			if(id >= MessageStyle.values().length) {
////				id = 0;
////			}
////			MessageStyle newStyle = MessageStyle.values()[id];
////			activeChannel.setMessageStyle(newStyle);
////			btnStyle.displayString = newStyle.name();
////			if(oldStyle == MessageStyle.Chat || newStyle == MessageStyle.Chat) {
////				if(ChatViewManager.getActiveView() == activeChannel) {
////					ChatViewManager.setActiveView(ChatViewManager.getNextChatView(activeChannel));
////				}
////			}
////		} else if(button == btnAddChannel) {
////			apply(false);
////			ChatViewOld view = new ChatViewOld(I18n.format(ChatTweaks.MOD_ID + ":gui.tabSettings.untitled"));
////			ChatViewManager.addChatView(view);
////			ChatViewManager.setActiveView(view);
////			selectChannel(view);
////		} else if(button == btnPrevChannel) {
////			apply(false);
////			// TODO fix me
//////			Collection<ChatView> views = ChatViewManager.getViews();
//////			int index = views.indexOf(activeChannel);
//////			index--;
//////			if(index < 0) {
//////				index = views.size() - 1;
//////			}
//////			selectChannel(views.get(index));
////		} else if(button == btnNextChannel) {
////			apply(false);
////			// TODO fix me
//////			List<ChatView> channels = ChatTweaks.getChatDisplay().getViews();
//////			int index = channels.indexOf(activeChannel);
//////			index++;
//////			if(index >= channels.size()) {
//////				index = 0;
//////			}
//////			selectChannel(channels.get(index));
////		} else if(button == btnDeleteChannel && !btnDeleteChannelConfirm.visible) {
////			btnDeleteChannelConfirm.visible = true;
////		} else if(button == btnDeleteChannelConfirm) {
////			ChatViewManager.removeChatView(activeChannel);
////			selectChannel(ChatViewManager.getNextChatView(null));
////		} else {
////			super.actionPerformed(button);
////		}
////	}
////
////	@Override
////	public void onGuiClosed() {
////		super.onGuiClosed();
////		apply(true);
////	}
////
////	@Override
////	protected void keyTyped(char typedChar, int keyCode) throws IOException {
////		super.keyTyped(typedChar, keyCode);
////		if(keyCode == Keyboard.KEY_ESCAPE) {
////			apply(true);
////		}
////	}
////
////	@Override
////	public void onLostFocus(GuiTextField textField) {
////		if(textField == txtLabel) {
////			apply(true);
////		} else if(textField == txtPattern || textField == txtFormat) {
////			apply(true);
////		} else if(textField == txtPrefix) {
////			apply(false);
////		}
////	}
////
////	public void apply(boolean refreshChat) {
////		activeChannel.setName(txtLabel.getText());
////		activeChannel.setFormat(txtFormat.getText());
////		activeChannel.setFilterPattern(txtPattern.getText());
////		activeChannel.setOutgoingPrefix(txtPrefix.getText());
////		activeChannel.setShowTimestamps(chkTimestamps.isChecked());
////		activeChannel.setMuted(chkMuted.isChecked());
////		activeChannel.setExclusive(chkExclusive.isChecked());
////		ChatViewManager.save();
////		if(refreshChat) {
////			// TODO fixme
//////			ChatTweaks.getChatDisplay().refreshChannel(activeChannel);
////			if(ChatViewManager.getActiveView() == activeChannel) {
////				ChatViewManager.setActiveView(activeChannel);
////			}
////		}
////	}
////
////	@Override
////	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
////		drawSimpleWindow();
////		// TODO fix me
//////		List<ChatView> channels = ChatTweaks.getChatDisplay().getViews();
//////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.settingsFor", "[" + txtLabel.getText() + "] (" + (channels.indexOf(activeChannel) + 1) + "/" + channels.size() + ")"), guiLeft + 4, guiTop + 4, 0xFFFFFFFF);
////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.tabLabel"), guiLeft + 8, guiTop + 22, 0xFFFFFFFF);
////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.filterPattern"), guiLeft + 8, guiTop + 56, 0xFFFFFFFF);
////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.outgoingPrefix"), guiLeft + 118, guiTop + 22, 0xFFFFFFFF);
////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.messageFormat"), guiLeft + 8, guiTop + 104, 0xFFFFFFFF);
////		mc.fontRendererObj.drawStringWithShadow(I18n.format("betterminecraftchat:gui.tabSettings.style"), guiLeft + 174, guiTop + 104, 0xFFFFFFFF);
////		super.drawScreen(mouseX, mouseY, partialTicks);
////		if(btnRegExHelp.isMouseOver()) {
////			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + I18n.format("betterminecraftchat:gui.tabSettings.requiredGroups"), " " + I18n.format("betterminecraftchat:gui.tabSettings.sender") + ": (?<s> ... )", " " + I18n.format("betterminecraftchat:gui.tabSettings.message") + ": (?<m> ... )"/*, TextFormatting.YELLOW + I18n.format("betterminecraftchat:gui.tabSettings.openHelpInBrowser")*/), mouseX, mouseY);
////		} else if(btnFormatHelp.isMouseOver()) {
////			GuiUtils.drawTooltip(Lists.newArrayList(TextFormatting.GOLD + I18n.format("betterminecraftchat:gui.tabSettings.variables"), " $0: " + I18n.format("betterminecraftchat:gui.tabSettings.originalText"), " ${s}: " + I18n.format("betterminecraftchat:gui.tabSettings.sender"), " ${m}: " + I18n.format("betterminecraftchat:gui.tabSettings.message"), " $...: " + I18n.format("betterminecraftchat:gui.tabSettings.customGroups")/*, TextFormatting.YELLOW + I18n.format("betterminecraftchat:gui.tabSettings.openHelpInBrowser")*/), mouseX, mouseY);
////		} else if(btnPrefixHelp.isMouseOver()) {
////			List<String> list = Lists.newArrayList();
////			list.add(TextFormatting.GOLD + I18n.format("betterminecraftchat:gui.tabSettings.messagePrefix"));
////			String[] lines = I18n.format("betterminecraftchat:gui.tabSettings.messagePrefixDesc").split("\\\\n");
////			Collections.addAll(list, lines);
////			GuiUtils.drawTooltip(list, mouseX, mouseY);
////		} else if(btnStyle.isMouseOver()) {
////			String styleLangKey = null;
////			switch(activeChannel.getMessageStyle()) {
////				case Hidden:
////					styleLangKey = "styleHidden";
////					break;
////				case Chat:
////					styleLangKey = "styleChat";
////					break;
////				case Side:
////					styleLangKey = "styleSideBar";
////					break;
////				case Bottom:
////					styleLangKey = "styleBottom";
////					break;
////			}
////			List<String> list = Lists.newArrayList();
////			list.add(TextFormatting.AQUA + I18n.format("betterminecraftchat:gui.tabSettings." + styleLangKey));
////			String[] lines = I18n.format("betterminecraftchat:gui.tabSettings." + styleLangKey + "Desc").split("\\\\n");
////			Collections.addAll(list, lines);
////			list.add(TextFormatting.YELLOW + I18n.format("betterminecraftchat:gui.tabSettings.clickToToggle"));
////			GuiUtils.drawTooltip(list, mouseX, mouseY);
////		}
////		GlStateManager.disableLighting();
////	}
////
////	public void selectChannel(ChatViewOld channel) {
////		this.activeChannel = channel;
////		btnDeleteChannelConfirm.visible = false;
////		txtLabel.setText(activeChannel.getName());
////		txtPattern.setText(activeChannel.getFilterPattern());
////		if(activeChannel.getOutgoingPrefix() != null) {
////			txtPrefix.setText(activeChannel.getOutgoingPrefix());
////		}
////		txtFormat.setText(activeChannel.getFormat());
////		txtPrefix.setText(activeChannel.getOutgoingPrefix() != null ? activeChannel.getOutgoingPrefix() : "");
////		btnStyle.displayString = activeChannel.getMessageStyle().name();
////		chkTimestamps.setIsChecked(activeChannel.isShowTimestamp());
////		chkMuted.setIsChecked(activeChannel.isMuted());
////		chkExclusive.setIsChecked(activeChannel.isExclusive());
////
////	}
////
////	public ChatViewOld getSelectedChannel() {
////		return activeChannel;
////	}
////
////	@Override
////	public String getNavigationId() {
////		return "settings";
////	}
//}
