package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.api.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.compat.BlurCompat;
import net.blay09.mods.chattweaks.imagepreview.PatternImageURLTransformer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.io.File;

public class ChatTweaks {

    public static final String TEXT_FORMATTING_RGB = "\u00a7#";
    public static final String TEXT_FORMATTING_EMOTE = "\u00a7*";

    public static final int MAX_MESSAGES = 100;

    public ChatTweaks() {
        ChatManager.init();

        File chatTweaksData = new File(Minecraft.getInstance().gameDir, "ChatTweaks");
        if (!chatTweaksData.exists() && !chatTweaksData.mkdirs()) {
            logger.error("Failed to create ChatTweaks data directory.");
        }

        // TODO postInit:
        ChatViewManager.load();
        BlurCompat.enableBlurCompat();
    }

    private void setupClient(FMLClientSetupEvent event) {
        // persistentChatGUI = new GuiNewChatExt(Minecraft.getInstance());
    }

    public static ChatMessage createChatMessage(ITextComponent component) {
        return new ChatMessage(ChatManager.getNextMessageId(), component);
    }

    public static void addChatMessage(ChatMessage chatMessage, @Nullable ChatChannel chatChannel) {
        // TODO persistentChatGUI.addChatMessage(chatMessage, chatChannel != null ? chatChannel : ChatManager.findChatChannel(chatMessage));
    }

    public static void addChatMessage(ITextComponent component, @Nullable ChatChannel chatChannel) {
        addChatMessage(createChatMessage(component), chatChannel);
    }

    public static void refreshChat() {
        for (ChatView chatView : ChatViewManager.getViews()) {
            chatView.refresh();
        }

        // TODO persistentChatGUI.refreshChat();
    }

}
