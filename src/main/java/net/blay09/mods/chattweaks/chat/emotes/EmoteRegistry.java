package net.blay09.mods.chattweaks.chat.emotes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.chattweaks.event.ReloadEmotes;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmoteRegistry {

    private static final Map<String, IEmoteGroup> groupMap = Maps.newHashMap();
    private static final Map<String, IEmote> emoteMap = Maps.newHashMap();
    private static final List<String> commonEmoteCodes = Lists.newArrayList();
    private static final List<IEmote> disposalList = Lists.newArrayList();
    public static boolean isLoading;

    public static <T> IEmote<T> registerEmote(String name, IEmoteSource<T> source, T data) {
        IEmote<T> emote = new Emote<>(name, source, data);
        emoteMap.put(emote.getCode(), emote);
        if (source.isCommonEmote(emote)) {
            commonEmoteCodes.add(name);
        }
        return emote;
    }

    public static IEmoteGroup registerEmoteGroup(String name) {
        IEmoteGroup group = new EmoteGroup(name);
        groupMap.put(name, group);
        return group;
    }

    public static void discardEmoteGroup(IEmoteGroup group) {
        for (IEmote emote : group.getEmotes()) {
            emoteMap.remove(emote.getCode());
            commonEmoteCodes.remove(emote.getCode());
        }
        groupMap.remove(group.getName());
    }

    @Nullable
    public static IEmoteGroup getFirstGroup() {
        if (isLoading) {
            return null;
        }
        return groupMap.values().iterator().next();
    }

    @Nullable
    public static IEmoteGroup getGroup(String name) {
        if (isLoading) {
            return null;
        }
        return groupMap.get(name);
    }

    @Nullable
    public static IEmote fromName(String name) {
        if (isLoading) {
            return null;
        }
        return emoteMap.get(name);
    }

    public static void reloadEmoticons() {
        synchronized (disposalList) {
            disposalList.addAll(emoteMap.values());
        }
        emoteMap.clear();
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

    public static Collection<String> getCommonEmoteCodes() {
        if (isLoading) {
            return Collections.emptyList();
        }
        return commonEmoteCodes;
    }

    public static Collection<String> getEmoteCodes() {
        if (isLoading) {
            return Collections.emptyList();
        }
        return emoteMap.keySet();
    }

    public static Collection<IEmote> getEmotes() {
        if (isLoading) {
            return Collections.emptyList();
        }
        return emoteMap.values();
    }

    public static Collection<IEmote> getEmotesByGroup(String group) {
        if (isLoading) {
            return Collections.emptyList();
        }
        IEmoteGroup emoteGroup = groupMap.get(group);
        if (emoteGroup != null) {
            return emoteGroup.getEmotes();
        }
        return Collections.emptyList();
    }

    public static boolean hasGroup(String group) {
        if (isLoading) {
            return false;
        }
        return groupMap.containsKey(group);
    }
}
