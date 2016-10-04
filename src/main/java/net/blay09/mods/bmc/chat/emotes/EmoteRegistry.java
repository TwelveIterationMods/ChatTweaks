package net.blay09.mods.bmc.chat.emotes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.bmc.event.ReloadEmotes;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmoteRegistry {

	private static final Map<String, IEmoteGroup> groupMap = Maps.newHashMap();
	private static final Map<String, IEmote> emoteMap = Maps.newHashMap();
	private static final List<IEmote> regexEmotes = Lists.newArrayList();
	private static final List<IEmote> disposalList = Lists.newArrayList();

	public static IEmote registerEmote(String name, IEmoteLoader loader) {
		IEmote emote = new Emote(name, loader, false);
		emoteMap.put(emote.getCode(), emote);
		return emote;
	}

	public static IEmote registerRegexEmote(String regex, IEmoteLoader loader) {
		IEmote emote = new Emote(regex, loader, true);
		regexEmotes.add(emote);
		return emote;
	}

	public static IEmoteGroup registerEmoteGroup(String name) {
		IEmoteGroup group = new EmoteGroup(name);
		groupMap.put(name, group);
		return group;
	}

	public static IEmoteGroup getFirstGroup() {
		return groupMap.values().iterator().next();
	}

	public static IEmoteGroup getGroup(String name) {
		return groupMap.get(name);
	}

	public static IEmote fromName(String name) {
		return emoteMap.get(name);
	}

	public static void reloadEmoticons() {
		synchronized (disposalList) {
			for (IEmote emote : emoteMap.values()) {
				disposalList.add(emote);
			}
			for (IEmote emote : regexEmotes) {
				disposalList.add(emote);
			}
		}
		emoteMap.clear();
		regexEmotes.clear();
		MinecraftForge.EVENT_BUS.post(new ReloadEmotes());
	}

	public static void runDisposal() {
		synchronized (disposalList) {
			if (!disposalList.isEmpty()) {
				for (IEmote emote : disposalList) {
					emote.getImage().disposeTexture();
				}
				disposalList.clear();
			}
		}
	}

	public static Collection<String> getEmoteCodes() {
		return emoteMap.keySet();
	}

	public static Collection<IEmote> getRegexEmotes() {
		return regexEmotes;
	}

	public static Collection<IEmote> getEmotes() {
		return emoteMap.values();
	}

	public static Collection<IEmote> getEmotesByGroup(String group) {
		IEmoteGroup emoteGroup = groupMap.get(group);
		if (emoteGroup != null) {
			return emoteGroup.getEmotes();
		}
		return Collections.emptyList();
	}

	public static boolean hasGroup(String group) {
		return groupMap.containsKey(group);
	}
}
