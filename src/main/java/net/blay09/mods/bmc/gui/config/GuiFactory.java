package net.blay09.mods.bmc.gui.config;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatViewManager;
import net.blay09.mods.bmc.IntegrationModule;
import net.blay09.mods.bmc.balyware.BlayCommon;
import net.blay09.mods.bmc.chat.ChatView;
import net.blay09.mods.bmc.compat.Compat;
import net.blay09.mods.bmc.gui.GuiOpenIntegrationLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiEditArray;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft mc) {
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new ConfigGUI(parentScreen);
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGUI.class;
	}

	@Override
	@Nullable
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

			list.add(GuiChatViewsConfig.getDummyElement());

			list.add(new DummyConfigElement.DummyCategoryElement("Twitch Integration", "config.category.twitchintegration", TwitchIntegrationButtonEntry.class));
			return list;
		}
	}

	public static class TwitchIntegrationButtonEntry extends GuiConfigEntries.CategoryEntry implements GuiYesNoCallback {
		public TwitchIntegrationButtonEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			if(Loader.isModLoaded(Compat.TWITCH_INTEGRATION)) {
				IModGuiFactory factory = FMLClientHandler.instance().getGuiFactoryFor(Loader.instance().getIndexedModList().get(Compat.TWITCH_INTEGRATION));
				return factory.createConfigGui(owningScreen);
			}
			return new GuiOpenIntegrationLink(this, "Twitch", "Chat Tweaks - Twitch Integration", 0);
		}

		@Override
		public void confirmClicked(boolean result, int id) {
			if(result) {
				try {
					BlayCommon.openWebLink(new URI("http://minecraft.curseforge.com/projects/twitch-integration"));
				} catch (URISyntaxException ignored) {}
			}
			mc.displayGuiScreen(owningScreen);
		}
	}

}
