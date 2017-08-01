package net.blay09.mods.chattweaks.event;

import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PrintChatMessageEvent extends Event {
	private final ChatMessage chatMessage;
	private final ChatView view;

	public PrintChatMessageEvent(ChatMessage chatMessage, ChatView view) {
		this.chatMessage = chatMessage;
		this.view = view;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public ChatView getView() {
		return view;
	}
}
