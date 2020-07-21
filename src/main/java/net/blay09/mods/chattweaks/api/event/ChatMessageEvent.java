package net.blay09.mods.chattweaks.api.event;

import net.blay09.mods.chattweaks.api.ChatMessage;
import net.minecraftforge.eventbus.api.Event;

public class ChatMessageEvent extends Event {

    private final ChatMessage chatMessage;

    public ChatMessageEvent(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
