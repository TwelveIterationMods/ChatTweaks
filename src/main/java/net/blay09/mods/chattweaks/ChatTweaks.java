package net.blay09.mods.chattweaks;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.auth.AuthManager;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.chat.emotes.twitch.TwitchEmotesAPI;
import net.blay09.mods.chattweaks.compat.BlurCompat;
import net.blay09.mods.chattweaks.compat.Compat;
import net.blay09.mods.chattweaks.gui.chat.GuiChatExt;
import net.blay09.mods.chattweaks.gui.chat.GuiNewChatExt;
import net.blay09.mods.chattweaks.gui.chat.GuiSleepMPExt;
import net.blay09.mods.chattweaks.gui.BottomChatRenderer;
import net.blay09.mods.chattweaks.gui.SideChatRenderer;
import net.blay09.mods.chattweaks.handler.EmoteTabCompletionHandler;
import net.blay09.mods.chattweaks.handler.HighlightHandler;
import net.blay09.mods.chattweaks.handler.OpenScreenshotFolderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.function.Function;

@Mod(modid = ChatTweaks.MOD_ID, name = "Chat Tweaks", clientSideOnly = true, guiFactory = "net.blay09.mods.chattweaks.gui.config.GuiFactory", acceptedMinecraftVersions = "[1.12]")
public class ChatTweaks {

    public static final String MOD_ID = "chattweaks";
    public static final String TEXT_FORMATTING_RGB = "\u00a7#";
    public static final String TEXT_FORMATTING_EMOTE = "\u00a7*";

    public static Logger logger;

    @Mod.Instance(MOD_ID)
    public static ChatTweaks instance;

    public static final KeyBinding keySwitchChatView = new KeyBinding("key.chattweaks.switch_chat_view", KeyConflictContext.GUI, KeyModifier.SHIFT, Keyboard.KEY_TAB, "key.categories.chattweaks");
    public static final KeyBinding keyOpenSettings = new KeyBinding("key.chattweaks.open_settins", KeyConflictContext.GUI, KeyModifier.ALT, Keyboard.KEY_C, "key.categories.chattweaks");

    private Configuration config;
    private GuiNewChatExt persistentChatGUI;
    private SideChatRenderer sideChatRenderer;
    private BottomChatRenderer bottomChatRenderer;
    private AuthManager authManager;
    private List<Function<String, String>> imageURLTransformers = Lists.newArrayList();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register((sideChatRenderer = new SideChatRenderer()));
        MinecraftForge.EVENT_BUS.register((bottomChatRenderer = new BottomChatRenderer()));
        MinecraftForge.EVENT_BUS.register(new EmoteTabCompletionHandler());
        MinecraftForge.EVENT_BUS.register(new HighlightHandler());
        MinecraftForge.EVENT_BUS.register(new OpenScreenshotFolderHandler());

        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        ChatTweaksConfig.preInitLoad(config);
        ChatManager.init();

        authManager = new AuthManager();
        authManager.load();

        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".+\\.(?:png|jpg)", "%s"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*imgur\\.com/[A-Za-z]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer("(.*twimg\\.com/[^:]+):large", "%s"));

        File configDir = new File(event.getModConfigurationDirectory(), "ChatTweaks");
        if (!configDir.exists() && !configDir.mkdirs()) {
            logger.error("Failed to create ChatTweaks config directory.");
        }

        File cacheDir = new File(Minecraft.getMinecraft().mcDataDir, "ChatTweaks/cache/");
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            logger.error("Failed to create ChatTweaks cache directory.");
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(keySwitchChatView);
        ClientRegistry.registerKeyBinding(keyOpenSettings);
        persistentChatGUI = new GuiNewChatExt(Minecraft.getMinecraft());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        ChatTweaksConfig.postInitLoad(config);
        ChatViewManager.load();

        if (config.hasChanged()) {
            config.save();
        }

        if (Loader.isModLoaded(Compat.BLUR)) {
            BlurCompat.enableBlurCompat();
        }
    }

    @SubscribeEvent
    public void onConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft.getMinecraft().ingameGUI.persistantChatGUI = persistentChatGUI;
    }

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.getGui() != null) {
            if (event.getGui().getClass() == GuiChat.class) {
                event.setGui(new GuiChatExt(((GuiChat) event.getGui()).defaultInputFieldText));
            } else if (event.getGui().getClass() == GuiSleepMP.class) {
                event.setGui(new GuiSleepMPExt());
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MOD_ID)) {
            if ("config".equals(event.getConfigID())) {
                ChatViewManager.save();
                ChatTweaksConfig.preInitLoad(config);
                ChatTweaksConfig.postInitLoad(config);
            }
        }
    }

    public static GuiNewChatExt getChatDisplay() {
        return instance.persistentChatGUI;
    }

    public static SideChatRenderer getSideChatHandler() {
        return instance.sideChatRenderer;
    }

    public static BottomChatRenderer getBottomChatHandler() {
        return instance.bottomChatRenderer;
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

    public static ChatMessage createChatMessage(ITextComponent component) {
        return new ChatMessage(ChatManager.getNextMessageId(), component);
    }

    public static void addChatMessage(ChatMessage chatMessage, @Nullable ChatChannel chatChannel) {
        instance.persistentChatGUI.addChatMessage(chatMessage, chatChannel != null ? chatChannel : ChatManager.findChatChannel(chatMessage));
    }

    public static void addChatMessage(ITextComponent component, @Nullable ChatChannel chatChannel) {
        addChatMessage(createChatMessage(component), chatChannel);
    }

    public static void refreshChat() {
        for (ChatView chatView : ChatViewManager.getViews()) {
            chatView.refresh();
        }
        instance.persistentChatGUI.refreshChat();
    }

}
