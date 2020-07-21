package net.blay09.mods.chattweaks.api;

import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public interface InternalMethods {
    void registerImageURLTransformer(Function<String, String> urlTransformer);

    void refreshChat();

    ChatMessage createChatMessage(ITextComponent textComponent);

    void addChatMessage(ChatMessage chatMessage, ChatChannel chatChannel);
}
