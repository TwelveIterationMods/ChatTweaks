package net.blay09.mods.chattweaks.api;

public interface ChatDisplay {
    String getName();
    void addChatMessageForDisplay(ChatMessage chatMessage, ChatView view);
}
