package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.api.ChatChannel;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ChatChannelImpl implements ChatChannel {

    private final Map<Integer, ChatMessage> chatMessages = new HashMap<>();

    private final String name;
    private final String description;
    private final ResourceLocation iconTexture;
    private final Predicate<String> langKeyMatcher;

    public ChatChannelImpl(String name, String description, ResourceLocation iconTexture, @Nullable Predicate<String> langKeyMatcher) {
        this.name = name;
        this.description = description;
        this.iconTexture = iconTexture;
        this.langKeyMatcher = langKeyMatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public ResourceLocation getIconTexture() {
        return iconTexture;
    }

    @Override
    public Predicate<String> getLangKeyMatcher() {
        return langKeyMatcher;
    }

    @Override
    public Collection<ChatMessage> getChatMessages() {
        return chatMessages.values();
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage) {
        chatMessages.put(chatMessage.getChatLineId(), chatMessage);
    }

    @Override
    public void removeChatMessage(int chatLineId) {
        chatMessages.remove(chatLineId);
    }

    @Override
    public void clearChatMessages() {
        chatMessages.clear();
    }

}
