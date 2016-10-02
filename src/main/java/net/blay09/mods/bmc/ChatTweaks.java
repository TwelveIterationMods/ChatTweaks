package net.blay09.mods.bmc;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.api.SimpleImageURLTransformer;
import net.blay09.mods.bmc.chat.badges.PatronBadges;
import net.blay09.mods.bmc.chat.ChatMacros;
import net.blay09.mods.bmc.chat.emotes.twitch.*;
import net.blay09.mods.bmc.gui.chat.GuiNewChatExt;
import net.blay09.mods.bmc.gui.settings.GuiTabSettings;
import net.blay09.mods.bmc.handler.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod(modid = ChatTweaks.MOD_ID, name = "Chat Tweaks", clientSideOnly = true, guiFactory = "net.blay09.mods.bmc.gui.GuiFactory")
public class ChatTweaks {

	public static final String MOD_ID = "betterminecraftchat";
	public static final String TWITCH_INTEGRATION = "twitchintegration";

	@Mod.Instance(MOD_ID)
    public static ChatTweaks instance;

	private final KeyBinding keyBindOptions = new KeyBinding("key.chattweaks.options", KeyConflictContext.IN_GAME, Keyboard.KEY_I, "key.category.chattweaks");

	private Configuration config;
	private GuiNewChatExt chatHandler;
	private GuiChatHandler guiChatHandler;
	private RenderHandler renderHandler;
	private SideChatHandler sideChatHandler;
	private BottomChatHandler bottomChatHandler;
	private AuthManager authManager;
	private List<Function<String, String>> imageURLTransformers = Lists.newArrayList();
	private Map<String, IntegrationModule> moduleMap = Maps.newHashMap();

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register((renderHandler = new RenderHandler()));
		MinecraftForge.EVENT_BUS.register((guiChatHandler = new GuiChatHandler()));
		MinecraftForge.EVENT_BUS.register((sideChatHandler = new SideChatHandler()));
		MinecraftForge.EVENT_BUS.register((bottomChatHandler = new BottomChatHandler()));

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
		ChatTweaksConfig.preInitLoad(config);
		authManager = new AuthManager();
		authManager.load();

		BetterMinecraftChatAPI._internal_setupAPI(new InternalMethodsImpl());

		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".+\\.(?:png|jpg)", ""));
		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*imgur\\.com/[A-Za-z]+", ".png"));
		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", ".png"));

		//noinspection ResultOfMethodCallIgnored
		new File(event.getModConfigurationDirectory(), "ChatTweaks").mkdirs();

		ChatMacros.load(new File(event.getModConfigurationDirectory(), "ChatTweaks/macros.ini"));

		ClientRegistry.registerKeyBinding(keyBindOptions);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		//noinspection ResultOfMethodCallIgnored
		new File(Minecraft.getMinecraft().mcDataDir, "bmc/cache/").mkdirs();

		chatHandler = new GuiNewChatExt(Minecraft.getMinecraft());
	}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		TwitchAPI.init();

		PatronBadges.init();

		ChatTweaksConfig.postInitLoad(config);

		if(config.hasChanged()) {
			config.save();
		}
    }

	@SubscribeEvent
	public void onConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		Minecraft.getMinecraft().ingameGUI.persistantChatGUI = chatHandler;
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(Keyboard.getEventKeyState() && keyBindOptions.isActiveAndMatches(Keyboard.getEventKey())) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTabSettings(null));
		}
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(MOD_ID)) {
			if(event.getConfigID().equals("config")) {
				ChatTweaksConfig.preInitLoad(config);
				ChatTweaksConfig.postInitLoad(config);
			}
		}
	}

	public static GuiNewChatExt getChatHandler() {
		return instance.chatHandler;
	}

	public static RenderHandler getRenderHandler() {
		return instance.renderHandler;
	}

	public static GuiChatHandler getGuiChatHandler() {
		return instance.guiChatHandler;
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

	public static Collection<Function<String, String>> getImageURLTransformers() {
		return instance.imageURLTransformers;
	}

	public static void registerImageURLTransformer(Function<String, String> function) {
		instance.imageURLTransformers.add(function);
	}

	public static int colorFromHex(String hex) {
		return Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
	}

	public static IntegrationModule getModule(String id) {
		return instance.moduleMap.get(id);
	}

	public static void registerIntegration(IntegrationModule module) {
		instance.moduleMap.put(module.getModId(), module);
	}
}
