package net.blay09.mods.bmc.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.balyware.gui.FormattedFontRenderer;
import net.blay09.mods.bmc.balyware.gui.GuiFormattedTextField;
import net.blay09.mods.bmc.balyware.gui.IStringFormatter;
import net.blay09.mods.bmc.chat.ChatView;
import net.blay09.mods.bmc.chat.MessageStyle;
import net.blay09.mods.bmc.gui.settings.FormatStringFormatter;
import net.blay09.mods.bmc.gui.settings.RegExStringFormatter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class GuiChatView extends GuiConfig {

	private final ChatView chatView;

	public GuiChatView(GuiScreen parentScreen, ChatView chatView) {
		super(parentScreen, getConfigElements(chatView), ChatTweaks.MOD_ID, "configIdlibus", false, false, "Add or Edit whateverus");
		this.chatView = chatView;
	}

	public static List<IConfigElement> getConfigElements(ChatView chatView) {
		List<IConfigElement> list = Lists.newArrayList();
		list.add(new DummyConfigElement("Name", chatView.getName(), ConfigGuiType.STRING, "chattweaks:gui.config.view.name"));

		DummyConfigElement filterPatternElement = new DummyConfigElement("Filter Pattern", "", ConfigGuiType.STRING, "chattweaks:gui.config.view.filter_pattern");
		filterPatternElement.set(chatView.getFilterPattern());
		filterPatternElement.setConfigEntryClass(RegexStringEntry.class);
		list.add(filterPatternElement);

		DummyConfigElement outputFormatElement = new DummyConfigElement("Output Format", "$0", ConfigGuiType.STRING, "chattweaks:gui.config.view.output_format");
		outputFormatElement.set(chatView.getOutputFormat());
		outputFormatElement.setConfigEntryClass(OutputFormatStringEntry.class);
		list.add(outputFormatElement);

		MessageStyle[] styles = MessageStyle.values();
		String[] styleNames = new String[styles.length];
		for (int i = 0; i < styles.length; i++) {
			styleNames[i] = styles[i].name();
		}
		DummyConfigElement messageStyleElement = new DummyConfigElement("Message Style", "Chat", ConfigGuiType.STRING, "chattweaks:gui.config.view.message_style", styleNames);
		messageStyleElement.set(chatView.getMessageStyle().name());
		list.add(messageStyleElement);

		DummyConfigElement outgoingPrefixElement = new DummyConfigElement("Outgoing Prefix", "", ConfigGuiType.STRING, "chattweaks:gui.config.view.outgoing_prefix");
		outgoingPrefixElement.set(chatView.getOutgoingPrefix());
		list.add(outgoingPrefixElement);

		DummyConfigElement exclusiveElement = new DummyConfigElement("Exclusive", false, ConfigGuiType.BOOLEAN, "chattweaks:gui.config.view.exclusive");
		exclusiveElement.set(chatView.isExclusive());
		list.add(exclusiveElement);

		DummyConfigElement mutedElement = new DummyConfigElement("Muted", false, ConfigGuiType.BOOLEAN, "chattweaks:gui.config.view.muted");
		mutedElement.set(chatView.isMuted());
		list.add(mutedElement);

		DummyConfigElement.DummyListElement channelListElement = new DummyConfigElement.DummyListElement("Channels", new String[0], ConfigGuiType.STRING, "chattweaks:gui.config.view.channels");
		channelListElement.setArrayEntryClass(ChannelListArrayEntry.class);
		channelListElement.setCustomEditListEntryClass(ChannelListArrayEntry.class);
		list.add(channelListElement);
		return list;
	}

	public static abstract class FormattedStringEntry extends GuiConfigEntries.ListEntryBase {

		private final GuiFormattedTextField textField;
		private final String beforeValue;

		public FormattedStringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement, IStringFormatter formatter, String displayTextWhenEmpty) {
			super(owningScreen, owningEntryList, configElement);

			textField = new GuiFormattedTextField(10, new FormattedFontRenderer(mc, mc.fontRenderer, formatter), owningEntryList.controlX + 1, 0, owningEntryList.controlWidth - 3, 16);
			textField.setDisplayTextWhenEmpty(displayTextWhenEmpty);
			textField.setMaxStringLength(10000);
			textField.setText(configElement.get().toString());
			beforeValue = configElement.get().toString();
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
			textField.xPosition = owningEntryList.controlX + 2;
			textField.yPosition = y + 1;
			textField.width = owningEntryList.controlWidth - 4;
			textField.setEnabled(enabled());
			textField.drawTextBox();
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) {
			if (enabled() || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END) {
				this.textField.textboxKeyTyped((enabled() ? eventChar : Keyboard.CHAR_NONE), eventKey);
				if (configElement.getValidationPattern() != null) {
					if (configElement.getValidationPattern().matcher(this.textField.getText().trim()).matches()) {
						isValidValue = true;
					} else {
						isValidValue = false;
					}
				}
			}
		}

		@Override
		public void updateCursorCounter() {
			textField.updateCursorCounter();
		}

		@Override
		public void mouseClicked(int x, int y, int mouseEvent) {
			textField.mouseClicked(x, y, mouseEvent);
		}

		@Override
		public boolean isDefault() {
			return configElement.getDefault() != null ? configElement.getDefault().toString().equals(textField.getText()) : textField.getText().trim().isEmpty();
		}

		@Override
		public void setToDefault() {
			if (enabled()) {
				textField.setText(configElement.getDefault() != null ? configElement.getDefault().toString() : "");
				keyTyped((char) Keyboard.CHAR_NONE, Keyboard.KEY_HOME);
			}
		}

		@Override
		public boolean isChanged() {
			return beforeValue != null ? !this.beforeValue.equals(textField.getText()) : this.textField.getText().trim().isEmpty();
		}

		@Override
		public void undoChanges() {
			if (enabled()) {
				textField.setText(beforeValue);
			}
		}

		@Override
		public boolean saveConfigElement() {
			if (enabled()) {
				if (isChanged() && isValidValue) {
					configElement.set(textField.getText());
					return configElement.requiresMcRestart();
				} else if (isChanged() && !isValidValue) {
					configElement.setToDefault();
					return configElement.requiresMcRestart() && beforeValue != null ? beforeValue.equals(configElement.getDefault()) : configElement.getDefault() == null;
				}
			}
			return false;
		}

		@Override
		public Object getCurrentValue() {
			return textField.getText();
		}

		@Override
		public Object[] getCurrentValues() {
			return new Object[]{getCurrentValue()};
		}
	}

	public static class OutputFormatStringEntry extends FormattedStringEntry {
		public OutputFormatStringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement, new FormatStringFormatter(), "(original)");
		}
	}

	public static class RegexStringEntry extends FormattedStringEntry {
		public RegexStringEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement, new RegExStringFormatter(), "(all messages)");
		}
	}

	public static class ChannelListArrayEntry extends GuiEditArrayEntries.BaseEntry {
		public ChannelListArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) {
			super.keyTyped(eventChar, eventKey);
			System.out.println("yes");
		}
	}

}
