package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.api.ChatTweaksAPI;
import net.blay09.mods.chattweaks.imagepreview.PatternImageURLTransformer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChatTweaks.MOD_ID)
public class ChatTweaks {
    public static final String MOD_ID = "chattweaks";
    public static final Logger logger = LogManager.getLogger();

    public ChatTweaks() {
        ChatTweaksAPI.__internalMethods = new InternalMethodsImpl();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ChatTweaksConfig.clientSpec);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);

        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".+\\.(?:png|jpg)", "%s"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*imgur\\.com/[A-Za-z]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer(".*gyazo\\.com/[a-z0-9]+", "%s.png"));
        ChatTweaksAPI.registerImageURLTransformer(new PatternImageURLTransformer("(.*twimg\\.com/[^:]+):large", "%s"));
    }

    private void setupClient(FMLClientSetupEvent event) {
        ModKeyBindings.register();
    }
}
