package net.blay09.mods.chattweaks.api;

public interface ChatDisplay {
    String getName();
    void addChatMessage(ChatMessage chatMessage, ChatView view);
}
