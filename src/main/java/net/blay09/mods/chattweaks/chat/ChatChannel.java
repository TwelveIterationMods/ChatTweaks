package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class ChatChannel {
	private final Map<Integer, ChatMessage> chatLines = Maps.newHashMap();
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
}
