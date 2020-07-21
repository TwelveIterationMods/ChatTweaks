package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.api.ChatChannel;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.api.InternalMethods;
import net.blay09.mods.chattweaks.core.ChatManager;
import net.blay09.mods.chattweaks.core.ChatMessageImpl;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.blay09.mods.chattweaks.imagepreview.ImageUrlTransformers;
import net.minecraft.util.text.ITextComponent;

import java.util.function.Function;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public void registerImageURLTransformer(Function<String, String> urlTransformer) {
        ImageUrlTransformers.registerImageURLTransformer(urlTransformer);
    }

    @Override
    public void refreshChat() {
        for (ChatView chatView : ChatViewManager.getViews()) {
            chatView.refresh();
        }

        // TODO refresh NewChatGui
    }

    @Override
    public ChatMessage createChatMessage(ITextComponent textComponent) {
        return new ChatMessageImpl(ChatManager.getNextMessageId(), textComponent);
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage, ChatChannel chatChannel) {
        ChatManager.addChatMessage(chatMessage, chatChannel);
    }
}
