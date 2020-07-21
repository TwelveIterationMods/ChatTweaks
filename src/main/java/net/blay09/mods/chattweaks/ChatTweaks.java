package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.api.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.ChatScreenReplacementHandler;
import net.blay09.mods.chattweaks.compat.BlurCompat;
import net.blay09.mods.chattweaks.core.ChatManager;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.blay09.mods.chattweaks.imagepreview.PatternImageURLTransformer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(ChatTweaks.MOD_ID)
public class ChatTweaks {
    public static final String MOD_ID = "chattweaks";
    public static final Logger logger = LogManager.getLogger();

    public ChatTweaks() {
        ChatTweaksAPI.__internalMethods = new InternalMethodsImpl();

        ChatManager.init();
        ChatViewManager.init();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChatTweaksConfig.clientSpec);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".+\\.(?:png|jpg)", "%s"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*imgur\\.com/[A-Za-z]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer("(.*twimg\\.com/[^:]+):large", "%s"));
    }

    private void setupClient(FMLClientSetupEvent event) {
        ModKeyBindings.register();

        BlurCompat.enableBlurCompat();
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ChatScreenReplacementHandler::replaceNewChatGui);
    }

    public static File getDataDir() {
        File dataDir = new File(Minecraft.getInstance().gameDir, "ChatTweaks");
        if (!dataDir.exists() && !dataDir.mkdirs()) {
            logger.error("Failed to create ChatTweaks data directory.");
        }
        return dataDir;
    }
}
