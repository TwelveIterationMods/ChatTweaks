package net.blay09.mods.bmc.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft mc) {}

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
			super(parentScreen, getCategories(), BetterMinecraftChat.MOD_ID, "config", false, false, "BetterMinecraftChat Settings");
		}

		private static List<IConfigElement> getCategories() {
			List<IConfigElement> list = Lists.newArrayList();
			list.add(new DummyConfigElement.DummyCategoryElement("General", "config.category.general", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("general")).getChildElements()));
			list.add(new DummyConfigElement.DummyCategoryElement("Emotes", "config.category.emotes", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("emotes")).getChildElements()));
			list.add(new DummyConfigElement.DummyCategoryElement("Theme", "config.category.theme", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("theme")).getChildElements()));
			return list;
		}
	}

}
