package net.blay09.mods.chattweaks.api;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Predicate;

public interface ChatChannel {
    String getName();

    String getDescription();

    ResourceLocation getIconTexture();

    @Nullable
    Predicate<String> getLangKeyMatcher();

    Collection<ChatMessage> getChatMessages();

    void addChatMessage(ChatMessage message);

    void removeChatMessage(int chatLineId);

    void clearChatMessages();
}
