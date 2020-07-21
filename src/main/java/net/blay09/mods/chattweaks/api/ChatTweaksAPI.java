package net.blay09.mods.chattweaks.api;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.function.Function;

public class ChatTweaksAPI {

    public static InternalMethods __internalMethods;

    public static void registerImageURLTransformer(Function<String, String> urlTransformer) {
        __internalMethods.registerImageURLTransformer(urlTransformer);
    }

    public static void refreshChat() {
        __internalMethods.refreshChat();
    }

    public static ChatMessage createChatMessage(ITextComponent textComponent) {
        return __internalMethods.createChatMessage(textComponent);
    }

    public static void addChatMessage(ITextComponent textComponent, @Nullable ChatChannel chatChannel) {
        addChatMessage(createChatMessage(textComponent), chatChannel);
    }

    public static void addChatMessage(ChatMessage chatMessage, @Nullable ChatChannel chatChannel) {
        __internalMethods.addChatMessage(chatMessage, chatChannel);
    }
}
