package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Map;

public class ChatChannel {
	private final Map<Integer, ChatMessage> chatMessages = Maps.newHashMap();
	private final String name;
	private final ResourceLocation icon;
	private final String description;
	private boolean enabled;

	public ChatChannel(String name, String description, ResourceLocation icon) {
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public ResourceLocation getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void addChatMessage(ChatMessage chatMessage) {
		chatMessages.put(chatMessage.getId(), chatMessage);
	}

	public void removeChatMessage(int chatMessageId) {
		chatMessages.remove(chatMessageId);
	}

	public Collection<ChatMessage> getChatMessages() {
		return chatMessages.values();
	}

	public void clearChatMessages() {
		chatMessages.clear();
	}
}
