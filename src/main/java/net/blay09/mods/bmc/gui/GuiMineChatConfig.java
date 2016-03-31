package net.blay09.mods.bmc.gui;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.List;

public class GuiMineChatConfig extends GuiConfig {
	public GuiMineChatConfig(GuiScreen parentScreen) {
		super(parentScreen, getCategories(), BetterMinecraftChat.MOD_ID, "config", false, false, "BetterMinecraftChat Settings");
	}

	private static List<IConfigElement> getCategories() {
		List<IConfigElement> list = Lists.newArrayList();
		list.add(new DummyConfigElement.DummyCategoryElement("General", "general", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("general")).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement("Emotes", "emotes", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("emotes")).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement("Theme", "theme", new ConfigElement(BetterMinecraftChat.getConfig().getCategory("theme")).getChildElements()));
		return list;
	}

}
