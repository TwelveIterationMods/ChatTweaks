package net.blay09.mods.bmc.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft mc) {
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGUI.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}

	public static class ConfigGUI extends GuiConfig {
		public ConfigGUI(GuiScreen parentScreen) {
			super(parentScreen, getCategories(), ChatTweaks.MOD_ID, "config", false, false, "Chat Tweaks Settings");
		}

		private static List<IConfigElement> getCategories() {
			List<IConfigElement> list = Lists.newArrayList();
			list.add(new DummyConfigElement.DummyCategoryElement("General", "config.category.general", new ConfigElement(ChatTweaks.getConfig().getCategory("general")).getChildElements()));
			list.add(new DummyConfigElement.DummyCategoryElement("Emotes", "config.category.emotes", new ConfigElement(ChatTweaks.getConfig().getCategory("emotes")).getChildElements()));
			list.add(new DummyConfigElement.DummyCategoryElement("Theme", "config.category.theme", new ConfigElement(ChatTweaks.getConfig().getCategory("theme")).getChildElements()));
			list.add(new DummyConfigElement.DummyCategoryElement("Views", "config.category.views", ViewsButtonEntryNew.class));
			return list;
		}
	}

	public static class ViewsButtonEntryNew extends GuiConfigEntries.CategoryEntry {

		public ViewsButtonEntryNew(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiChatViews(this.owningScreen);
		}

	}

	public static class ViewsButtonEntry implements GuiConfigEntries.IConfigEntry {

		private final Minecraft mc;
		private final GuiConfig owningScreen;
		private final GuiConfigEntries owningEntryList;
		private final IConfigElement configElement;
		private final GuiButtonExt button;

		public ViewsButtonEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			this.mc = Minecraft.getMinecraft();
			this.owningScreen = owningScreen;
			this.owningEntryList = owningEntryList;
			this.configElement = configElement;
			this.button = new GuiButtonExt(0, owningEntryList.controlX, 0, owningEntryList.controlWidth, 18, configElement.get() != null ? I18n.format(String.valueOf(configElement.get())) : "");
		}

		@Override
		public IConfigElement getConfigElement() {
			return configElement;
		}

		@Override
		public String getName() {
			return configElement.getName();
		}

		@Override
		public Object getCurrentValue() {
			return configElement.get();
		}

		@Override
		public Object[] getCurrentValues() {
			return configElement.getList();
		}

		@Override
		public boolean enabled() {
			return true;
		}

		@Override
		public void keyTyped(char eventChar, int eventKey) {

		}

		@Override
		public void updateCursorCounter() {

		}

		@Override
		public void mouseClicked(int x, int y, int mouseEvent) {

		}

		@Override
		public boolean isDefault() {
			return true;
		}

		@Override
		public void setToDefault() {
		}

		@Override
		public void undoChanges() {
		}

		@Override
		public boolean isChanged() {
			return false;
		}

		@Override
		public boolean saveConfigElement() {
			return false;
		}

		@Override
		public void drawToolTip(int mouseX, int mouseY) {
		}

		@Override
		public int getLabelWidth() {
			return 0;
		}

		@Override
		public int getEntryRightBound() {
			return 0;
		}

		@Override
		public void onGuiClosed() {

		}

		@Override
		public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

		}

		@Override
		public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
			this.button.width = this.owningEntryList.controlWidth;
			this.button.xPosition = this.owningScreen.entryList.controlX;
			this.button.yPosition = y;
			this.button.enabled = enabled();
			this.button.drawButton(this.mc, mouseX, mouseY);
		}

		@Override
		public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
			if (this.button.mousePressed(this.mc, mouseX, mouseY)) {
				this.button.playPressSound(this.mc.getSoundHandler());
				System.out.println("do the thing here");
				return true;
			}
			return false;
		}

		@Override
		public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
			this.button.mouseReleased(x, y);
		}
	}

}
