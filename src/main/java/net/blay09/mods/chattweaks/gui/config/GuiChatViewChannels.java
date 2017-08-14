package net.blay09.mods.chattweaks.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.ChatManager;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.List;

public class GuiChatViewChannels extends GuiEditArray {

	private final List<GuiChannelListEntry> availableChannels = Lists.newArrayList();
	private final List<GuiChannelListEntry> selectedChannels = Lists.newArrayList();
	private GuiChatViewChannelsList listAvailable;
	private GuiChatViewChannelsList listSelected;

	public GuiChatViewChannels(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Object[] currentValues, boolean enabled) {
		super(parentScreen, configElement, slotIndex, currentValues, enabled);
	}

	@Override
	public void initGui() {
		super.initGui();

		updateList();

		listAvailable = new GuiChatViewChannelsList(mc, 200, height, availableChannels, I18n.format("chattweaks:config.channels.available"));
		listAvailable.setSlotXBoundsFromLeft(width / 2 - 4 - 200);
		listAvailable.registerScrollButtons(7, 8);

		listSelected = new GuiChatViewChannelsList(mc, 200, height, selectedChannels, I18n.format("chattweaks:config.channels.selected"));
		listSelected.setSlotXBoundsFromLeft(width / 2 + 4);
		listSelected.registerScrollButtons(7, 8);
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		listAvailable.handleMouseInput();
		listSelected.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		listAvailable.mouseClicked(mouseX, mouseY, mouseButton);
		listSelected.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawBackground(0);

		btnDefault.enabled = enabled && !entryList.isDefault();
		btnUndoChanges.enabled = enabled && entryList.isChanged();

		listAvailable.drawScreen(mouseX, mouseY, partialTicks);
		listSelected.drawScreen(mouseX, mouseY, partialTicks);
		drawCenteredString(fontRenderer, "chattweaks:config.channels.select_channels", width / 2, 16, 0xFFFFFF);

		for (GuiButton button : buttonList) {
			button.drawButton(mc, mouseX, mouseY);
		}
	}

	public boolean isSelected(ChatChannel channel) {
		return ArrayUtils.contains(currentValues, channel.getName());
	}

	public void unselectChannel(GuiChannelListEntry entry) {
		currentValues = ArrayUtils.remove(currentValues, ArrayUtils.indexOf(currentValues, entry.getName()));
		selectedChannels.remove(entry);
		availableChannels.add(entry);
		recalculateState();
	}

	public void selectChannel(GuiChannelListEntry entry) {
		currentValues = ArrayUtils.add(currentValues, entry.getName());
		selectedChannels.add(entry);
		availableChannels.remove(entry);
		recalculateState();
	}

	public void recalculateState() {
		entryList.isDefault = true;
		entryList.isChanged = false;

		int listLength = configElement.isListLengthFixed() ? currentValues.length : currentValues.length - 1;

		if (listLength != configElement.getDefaults().length) {
			entryList.isDefault = false;
		}

		if (listLength != beforeValues.length) {
			entryList.isChanged = true;
		}

		if (entryList.isDefault) {
			for (int i = 0; i < listLength; i++) {
				if (!configElement.getDefaults()[i].equals(currentValues[i])) {
					entryList.isDefault = false;
				}
			}
		}

		if (!entryList.isChanged) {
			for (int i = 0; i < listLength; i++) {
				if (!beforeValues[i].equals(currentValues[i])) {
					entryList.isChanged = true;
				}
			}
		}
	}

	public void updateList() {
		selectedChannels.clear();
		availableChannels.clear();
		for (ChatChannel chatChannel : ChatManager.getChatChannels()) {
			if (ArrayUtils.contains(currentValues, chatChannel.getName())) {
				selectedChannels.add(new GuiChannelListEntry(mc, this, chatChannel));
			} else {
				availableChannels.add(new GuiChannelListEntry(mc, this, chatChannel));
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button == btnDone) {
			((GuiConfigEntries.ArrayEntry) ((GuiConfig) parentScreen).entryList.getListEntry(slotIndex)).setListFromChildScreen(currentValues);
//			((GuiConfig) parentScreen).entryList.getListEntry(slotIndex).saveConfigElement();
//			currentValues = selectedChannels.stream().map(GuiChannelListEntry::getName).toArray(String[]::new);
			mc.displayGuiScreen(parentScreen);
			return;
		}

		super.actionPerformed(button);

		updateList();
	}
}
