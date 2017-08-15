package net.blay09.mods.chattweaks.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.ChatManager;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatViewManager;
import net.blay09.mods.chattweaks.balyware.gui.FormattedFontRenderer;
import net.blay09.mods.chattweaks.balyware.gui.GuiFormattedTextField;
import net.blay09.mods.chattweaks.balyware.gui.IStringFormatter;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.chat.MessageStyle;
import net.blay09.mods.chattweaks.gui.settings.FormatStringFormatter;
import net.blay09.mods.chattweaks.gui.settings.RegExStringFormatter;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.List;

public class GuiChatView extends GuiConfig {

	private final ChatView chatView;

	private static SmartyConfigElement nameElement;
	private static SmartyConfigElement filterPatternElement;
	private static SmartyConfigElement outputFormatElement;
	private static SmartyConfigElement messageStyleElement;
	private static SmartyConfigElement outgoingPrefixElement;
	private static SmartyConfigElement exclusiveElement;
	private static SmartyConfigElement mutedElement;
	private static SmartyListElement channelListElement;
	private static String[] channelNames;

	public GuiChatView(@Nullable GuiScreen parentScreen, ChatView chatView) {
		super(parentScreen, getConfigElements(chatView), ChatTweaks.MOD_ID, "config", false, false, "Manage Chat Views");
		this.chatView = chatView;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 2000) {
			entryList.saveConfigElements();

			if (!nameElement.get().equals(chatView.getName())) {
				ChatViewManager.renameChatView(chatView, (String) nameElement.get());
			}
			chatView.setFilterPattern((String) filterPatternElement.get());
			chatView.setOutputFormat((String) outputFormatElement.get());
			chatView.setMessageStyle(MessageStyle.valueOf((String) messageStyleElement.get()));
			chatView.setOutgoingPrefix(!outgoingPrefixElement.get().equals("") ? (String) outgoingPrefixElement.get() : null);
			chatView.setExclusive((boolean) exclusiveElement.get());
			chatView.setMuted((boolean) mutedElement.get());
			chatView.setTemporary(false);

			ChatViewManager.save();

			mc.displayGuiScreen(parentScreen);
			return;
		}
		super.actionPerformed(button);
	}

	private static List<IConfigElement> getConfigElements(ChatView chatView) {
		List<IConfigElement> list = Lists.newArrayList();
		list.add(nameElement = new SmartyConfigElement("Name", chatView.getName(), ConfigGuiType.STRING, "chattweaks:gui.config.view.name"));

		filterPatternElement = new SmartyConfigElement("Filter Pattern", "", ConfigGuiType.STRING, "chattweaks:gui.config.view.filter_pattern");
		filterPatternElement.set(chatView.getFilterPattern());
		filterPatternElement.setConfigEntryClass(RegexStringEntry.class);
		list.add(filterPatternElement);

		outputFormatElement = new SmartyConfigElement("Output Format", "$0", ConfigGuiType.STRING, "chattweaks:gui.config.view.output_format");
		outputFormatElement.set(chatView.getOutputFormat());
		outputFormatElement.setConfigEntryClass(OutputFormatStringEntry.class);
		list.add(outputFormatElement);

		MessageStyle[] styles = MessageStyle.values();
		String[] styleNames = new String[styles.length];
		for (int i = 0; i < styles.length; i++) {
			styleNames[i] = styles[i].name();
		}
		messageStyleElement = new SmartyConfigElement("Message Style", "Chat", ConfigGuiType.STRING, "chattweaks:gui.config.view.message_style", styleNames);
		messageStyleElement.set(chatView.getMessageStyle().name());
		list.add(messageStyleElement);

		outgoingPrefixElement = new SmartyConfigElement("Outgoing Prefix", "", ConfigGuiType.STRING, "chattweaks:gui.config.view.outgoing_prefix");
		outgoingPrefixElement.set(chatView.getOutgoingPrefix() != null ? chatView.getOutgoingPrefix() : "");
		list.add(outgoingPrefixElement);

		exclusiveElement = new SmartyConfigElement("Exclusive", false, ConfigGuiType.BOOLEAN, "chattweaks:gui.config.view.exclusive");
		exclusiveElement.set(chatView.isExclusive());
		list.add(exclusiveElement);

		mutedElement = new SmartyConfigElement("Muted", false, ConfigGuiType.BOOLEAN, "chattweaks:gui.config.view.muted");
		mutedElement.set(chatView.isMuted());
		list.add(mutedElement);

		channelNames = chatView.getChannels().stream().map(ChatChannel::getName).toArray(String[]::new);
		channelListElement = new SmartyListElement("Channels", channelNames, ConfigGuiType.STRING, "chattweaks:gui.config.view.channels", true);
		channelListElement.set(channelNames);
		channelListElement.setConfigEntryClass(ChannelListConfigEntry.class);
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
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
			textField.x = owningEntryList.controlX + 2;
			textField.y = y + 1;
			textField.width = owningEntryList.controlWidth - 4;
			textField.setEnabled(enabled());
			textField.drawTextBox();
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) {
			if (enabled() || eventKey == Keyboard.KEY_LEFT || eventKey == Keyboard.KEY_RIGHT || eventKey == Keyboard.KEY_HOME || eventKey == Keyboard.KEY_END) {
				this.textField.textboxKeyTyped((enabled() ? eventChar : Keyboard.CHAR_NONE), eventKey);
				if (configElement.getValidationPattern() != null) {
					isValidValue = configElement.getValidationPattern().matcher(this.textField.getText().trim()).matches();
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

		private final GuiCheckBox checkBox;

		public ChannelListArrayEntry(GuiEditArray owningScreen, GuiEditArrayEntries owningEntryList, IConfigElement configElement, Object value) {
			super(owningScreen, owningEntryList, configElement);

			checkBox = new GuiCheckBox(0, 0, 0, "", Boolean.parseBoolean((String) value));
		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partial) {
			super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, partial);
			checkBox.x = listWidth / 4;
			checkBox.y = y;
			checkBox.displayString = " " + GuiChatView.channelNames[slotIndex];
			checkBox.drawButton(owningScreen.mc, mouseX, mouseY, partial);
		}

		@Override
		public boolean mousePressed(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			if (checkBox.mousePressed(owningEntryList.getMC(), x, y)) {
				checkBox.playPressSound(owningEntryList.getMC().getSoundHandler());
				owningEntryList.recalculateState();
				return true;
			}

			return super.mousePressed(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public void mouseReleased(int index, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			checkBox.mouseReleased(x, y);
			super.mouseReleased(index, x, y, mouseEvent, relativeX, relativeY);
		}

		@Override
		public Object getValue() {
			return checkBox.isChecked();
		}
	}

	public static class ChannelListConfigEntry extends GuiConfigEntries.ArrayEntry {
		public ChannelListConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		public void updateValueButtonText() {
			ChatView chatView = ((GuiChatView) owningScreen).chatView;
			if(chatView != null) {
				StringBuilder sb = new StringBuilder();

				for (Object value : currentValues) {
					if (sb.length() > 0) {
						sb.append(", ");
					}
					sb.append((String) value);
				}
				btnValue.displayString = sb.length() > 0 ? sb.toString() : TextFormatting.RED + "Warning: No channels selected";
			}
		}

		@Override
		public void valueButtonPressed(int slotIndex) {
			mc.displayGuiScreen(new GuiChatViewChannels(owningScreen, configElement, slotIndex, currentValues, true));
		}

		@Override
		public boolean saveConfigElement() {
			((GuiChatView) owningScreen).chatView.getChannels().clear();
			for(Object o : currentValues) {
				ChatChannel channel = ChatManager.getChatChannel((String) o);
				if(channel != null) {
					((GuiChatView) owningScreen).chatView.getChannels().add(channel);
				}
			}
			updateValueButtonText();

			return false;
		}
	}
}
