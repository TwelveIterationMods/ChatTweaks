package net.blay09.mods.bmc;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.SimpleImageURLTransformer;
import net.blay09.mods.bmc.chat.badges.PatronBadges;
import net.blay09.mods.bmc.chat.ChatMacros;
import net.blay09.mods.bmc.chat.emotes.twitch.*;
import net.blay09.mods.bmc.handler.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.util.Collection;
import java.util.List;

@Mod(modid = BetterMinecraftChat.MOD_ID, name = "BetterMinecraftChat", clientSideOnly = true, guiFactory = "net.blay09.mods.bmc.gui.GuiFactory",
	updateJSON = "http://balyware.com/new/forge_update.php?modid=" + BetterMinecraftChat.MOD_ID)
public class BetterMinecraftChat {

	public static final String MOD_ID = "betterminecraftchat";
	public static final String TWITCH_INTEGRATION = "twitchintegration";

	@Mod.Instance(MOD_ID)
    public static BetterMinecraftChat instance;

	private Configuration config;
	private int maxTextureSize;
	private ChatHandler chatHandler;
	private GuiChatHandler guiChatHandler;
	private RenderHandler renderHandler;
	private SideChatHandler sideChatHandler;
	private BottomChatHandler bottomChatHandler;
	private List<Function<String, String>> imageURLTransformers = Lists.newArrayList();

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register((chatHandler = new ChatHandler()));
		MinecraftForge.EVENT_BUS.register((renderHandler = new RenderHandler()));
		MinecraftForge.EVENT_BUS.register((guiChatHandler = new GuiChatHandler()));
		MinecraftForge.EVENT_BUS.register((sideChatHandler = new SideChatHandler()));
		MinecraftForge.EVENT_BUS.register((bottomChatHandler = new BottomChatHandler()));

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
		BetterMinecraftChatConfig.preInitLoad(config);
		AuthManager.load();

		BetterMinecraftChatAPI._internal_setupAPI(new InternalMethodsImpl());

		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".+\\.(?:png|jpg)", ""));
		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*imgur\\.com/[A-Za-z]+", ".png"));
		BetterMinecraftChatAPI.registerImageURLTransformer(new SimpleImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", ".png"));

		ChatMacros.load(new File(event.getModConfigurationDirectory(), "BetterMinecraftChat/macros.ini"));
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		//noinspection ResultOfMethodCallIgnored
		new File(Minecraft.getMinecraft().mcDataDir, "bmc/cache/").mkdirs();
	}

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        maxTextureSize = GL11.glGetInteger(GL11.GL_MAX_TEXTURE_SIZE);

		TwitchAPI.init();

		PatronBadges.init();

		BetterMinecraftChatConfig.postInitLoad(config);

		if(config.hasChanged()) {
			config.save();
		}
    }

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.getModID().equals(MOD_ID)) {
			if(event.getConfigID().equals("config")) {
				BetterMinecraftChatConfig.preInitLoad(config);
				BetterMinecraftChatConfig.postInitLoad(config);
			}
		}
	}

    public static int getMaxTextureSize() {
        return instance.maxTextureSize;
    }

	public static ChatHandler getChatHandler() {
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

	public static Collection<Function<String, String>> getImageURLTransformers() {
		return instance.imageURLTransformers;
	}

	public static void registerImageURLTransformer(Function<String, String> function) {
		instance.imageURLTransformers.add(function);
	}

	public static int colorFromHex(String hex) {
		return Integer.parseInt(hex.startsWith("#") ? hex.substring(1) : hex, 16);
	}
}
