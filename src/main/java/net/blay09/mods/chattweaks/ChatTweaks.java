package net.blay09.mods.chattweaks;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.auth.AuthManager;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.blay09.mods.chattweaks.chat.emotes.twitch.TwitchAPI;
import net.blay09.mods.chattweaks.gui.chat.GuiChatExt;
import net.blay09.mods.chattweaks.gui.chat.GuiNewChatExt;
import net.blay09.mods.chattweaks.gui.chat.GuiSleepMPExt;
import net.blay09.mods.chattweaks.handler.BottomChatHandler;
import net.blay09.mods.chattweaks.handler.SideChatHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.function.Function;

@Mod(modid = ChatTweaks.MOD_ID, name = "Chat Tweaks", clientSideOnly = true, guiFactory = "net.blay09.mods.chattweaks.gui.config.GuiFactory")
public class ChatTweaks {

	public static final String MOD_ID = "chattweaks";
	public static final String TEXT_FORMATTING_RGB = "\u00a7#";

	public static Logger logger;

	@Mod.Instance(MOD_ID)
    public static ChatTweaks instance;

	private Configuration config;
	private GuiNewChatExt persistentChatGUI;
	private SideChatHandler sideChatHandler;
	private BottomChatHandler bottomChatHandler;
	private AuthManager authManager;
	private List<Function<String, String>> imageURLTransformers = Lists.newArrayList();

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register((sideChatHandler = new SideChatHandler()));
		MinecraftForge.EVENT_BUS.register((bottomChatHandler = new BottomChatHandler()));

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
		ChatTweaksConfig.preInitLoad(config);

		authManager = new AuthManager();
		authManager.load();

		ChatTweaksAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".+\\.(?:png|jpg)", ""));
		ChatTweaksAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*imgur\\.com/[A-Za-z]+", ".png"));
		ChatTweaksAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", ".png"));

		File configDir = new File(event.getModConfigurationDirectory(), "ChatTweaks");
		if(!configDir.exists() && !configDir.mkdirs()) {
			logger.error("Failed to create ChatTweaks config directory.");
		}

		File cacheDir = new File(Minecraft.getMinecraft().mcDataDir, "ChatTweaks/cache/");
		if(!cacheDir.exists() && !cacheDir.mkdirs()) {
			logger.error("Failed to create ChatTweaks cache directory.");
		}
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		persistentChatGUI = new GuiNewChatExt(Minecraft.getMinecraft());
	}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		TwitchAPI.init();

		ChatTweaksConfig.postInitLoad(config);

		if(config.hasChanged()) {
			config.save();
		}
    }

	@SubscribeEvent
	public void onConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		Minecraft.getMinecraft().ingameGUI.persistantChatGUI = persistentChatGUI;
	}

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
		if(event.getGui() != null) {
			if (event.getGui().getClass() == GuiChat.class) {
				event.setGui(new GuiChatExt(((GuiChat) event.getGui()).defaultInputFieldText));
			} else if (event.getGui().getClass() == GuiSleepMP.class) {
				event.setGui(new GuiSleepMPExt());
			}
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(MOD_ID)) {
			if("config".equals(event.getConfigID())) {
				ChatViewManager.save();
				ChatTweaksConfig.preInitLoad(config);
				ChatTweaksConfig.postInitLoad(config);
			}
		}
	}

	public static GuiNewChatExt getChatDisplay() {
		return instance.persistentChatGUI;
	}

	public static SideChatHandler getSideChatHandler() {
		return instance.sideChatHandler;
	}

	public static BottomChatHandler getBottomChatHandler() {
		return instance.bottomChatHandler;
	}

	public static Configuration getConfig() {
		return instance.config;
	}

	public static AuthManager getAuthManager() {
		return instance.authManager;
	}

	public static List<Function<String, String>> getImageURLTransformers() {
		return instance.imageURLTransformers;
	}

	public static void registerImageURLTransformer(Function<String, String> function) {
		instance.imageURLTransformers.add(function);
	}

	public static int colorFromHex(String hex) {
		return Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
	}

	public static ChatMessage addChatMessage(ITextComponent component, @Nullable ChatChannel chatChannel) {
		ChatMessage chatMessage = new ChatMessage(ChatManager.getNextMessageId(), component);
		instance.persistentChatGUI.addChatMessage(chatMessage, chatChannel != null ? chatChannel : ChatManager.findChatChannel(chatMessage));
		return chatMessage;
	}

}
