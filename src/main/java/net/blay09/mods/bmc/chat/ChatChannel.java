package net.blay09.mods.bmc.chat;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sun.org.apache.xml.internal.utils.XMLString;

import java.util.Map;
import java.util.Set;

public class ChatChannel {
	private final Map<Integer, ChatMessage> chatLines = Maps.newHashMap();
	private String name;

	public ChatChannel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
