package net.blay09.mods.bmc.gui.config;

import net.blay09.mods.bmc.ChatViewManager;
import net.blay09.mods.bmc.chat.ChatView;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiChatViewsConfig extends GuiEditArray {

	public GuiChatViewsConfig(GuiScreen parentScreen, IConfigElement configElement, int slotIndex, Object[] currentValues, boolean enabled) {
		super(parentScreen, configElement, slotIndex, currentValues, enabled);
	}

	@Override
	public void initGui() {
		super.initGui();

		fixForge();
	}

	private void fixForge() {
		entryList = new GuiEditArrayEntries(this, mc, configElement, beforeValues, currentValues);
		entryList.listEntries.clear();
		boolean canDelete = currentValues.length > 1;
		for (Object chatView : currentValues) {
			entryList.listEntries.add(new ChatViewArrayEntry(this, entryList, configElement, chatView, canDelete));
		}
		entryList.listEntries.add(new GuiEditArrayEntries.BaseEntry(this, entryList, configElement));
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		if (button == btnUndoChanges) {
			fixForge();
		} else if(button == btnDefault) {
			fixForge();
		}
	}

	public static IConfigElement getDummyElement() {
		SmartyListElement dummy = new SmartyListElement("Views", ChatViewManager.createDefaults(), ConfigGuiType.STRING, "config.category.views");
		dummy.setConfigEntryClass(ChatViewConfigEntry.class);
		dummy.setArrayEntryClass(ChatViewArrayEntry.class);
		dummy.setCustomEditListEntryClass(ChatViewArrayEntry.class);
		dummy.set(ChatViewManager.getViews().toArray());
		return dummy;
	}

	public static class ChatViewConfigEntry extends GuiConfigEntries.ArrayEntry {
		protected final GuiButtonExt btnValueFixed;

		public ChatViewConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
			btnValue.visible = false;
			drawLabel = false;

			btnValueFixed = new GuiButtonExt(0, 0, 0, 300, 18, I18n.format(name));
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);

			btnValueFixed.xPosition = listWidth / 2 - 150;
			btnValueFixed.yPosition = y;
			btnValueFixed.enabled = enabled();
			btnValueFixed.drawButton(mc, mouseX, mouseY);
		}

		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if (btnValueFixed.mousePressed(mc, x, y)) {
				btnValueFixed.playPressSound(mc.getSoundHandler());
				valueButtonPressed(index);
				updateValueButtonText();
				return true;
			} else {
				return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
			}
		}

		@Override
		public void valueButtonPressed(int slotIndex) {
			mc.displayGuiScreen(new GuiChatViewsConfig(this.owningScreen, configElement, slotIndex, currentValues, enabled()));
		}

		@Override
		public boolean saveConfigElement() {
			ChatViewManager.removeAllChatViews();
			for(Object chatView : currentValues) {
				ChatViewManager.addChatView((ChatView) chatView);
			}
			return super.saveConfigElement();
		}
	}

	public static class ChatViewArrayEntry extends GuiEditArrayEntries.BaseEntry {
		protected final GuiButtonExt button;
		protected final ChatView chatView;

		public ChatViewArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value) {
			this(owningScreen, owningEntryList, configElement, value, true);
		}

		public ChatViewArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value, boolean canDelete) {
			super(owningScreen, owningEntryList, configElement);

			btnRemoveEntry.enabled = canDelete;

			if (value.equals("")) {
				chatView = new ChatView(ChatViewManager.getNextChatViewName());
			} else if (value instanceof ChatView) {
				chatView = (ChatView) value;
			} else {
				chatView = null;
			}

			button = new GuiButtonExt(0, 0, 0, owningEntryList.controlWidth, 18, I18n.format(String.valueOf(value)));
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);

			btnRemoveEntry.enabled = owningEntryList.listEntries.size() > 2;

			button.xPosition = listWidth / 4;
			button.yPosition = y;

			button.displayString = chatView != null ? chatView.getName() : "<3 Forge";

			button.drawButton(owningEntryList.getMC(), mouseX, mouseY);
		}

		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if (button.mousePressed(owningEntryList.getMC(), x, y)) {
				button.playPressSound(owningEntryList.getMC().getSoundHandler());
				owningScreen.mc.displayGuiScreen(new GuiChatView(owningScreen, chatView));
				return true;
			}

			return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			button.mouseReleased(x, y);
			super.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public Object getValue() {
			return chatView;
		}
	}
}
