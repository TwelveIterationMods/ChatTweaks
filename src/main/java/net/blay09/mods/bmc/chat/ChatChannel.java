package net.blay09.mods.bmc.chat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.org.apache.xml.internal.utils.XMLString;
import jline.internal.Nullable;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Set;

public class ChatChannel {
	private final Map<Integer, ChatMessage> chatLines = Maps.newHashMap();
	private final String name;
	private final ResourceLocation icon;
	private final String description;

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
}
