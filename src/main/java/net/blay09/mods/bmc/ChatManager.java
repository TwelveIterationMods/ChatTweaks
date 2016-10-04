package net.blay09.mods.bmc;

import com.google.common.collect.Maps;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.ChatMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatManager {
	private static final int START_ID = 500;
	private static final AtomicInteger chatLineCounter = new AtomicInteger(START_ID);
	private static final ChatChannel mainChannel = new ChatChannel("main");
	private static final ChatChannel interactionChannel = new ChatChannel("interaction");
	private static final ChatChannel systemChannel = new ChatChannel("system");
	private static final Map<String, ChatChannel> channels = Maps.newHashMap();

	public static void init() {
		addChatChannel(mainChannel);
		addChatChannel(interactionChannel);
		addChatChannel(systemChannel);
	}

	public static ChatChannel findChatChannel(ChatMessage message) {
		// TODO scan for system & interaction messages
		return mainChannel;
	}

	public static ChatChannel getChatChannel(String name) {
		return channels.get(name);
	}

	public static void addChatChannel(ChatChannel channel) {
		if(channels.containsKey(channel.getName())) {
			throw new RuntimeException("duplicate channel " + channel.getName());
		}
		channels.put(channel.getName(), channel);
	}

	public static int getNextMessageId() {
		return chatLineCounter.incrementAndGet();
	}

}
