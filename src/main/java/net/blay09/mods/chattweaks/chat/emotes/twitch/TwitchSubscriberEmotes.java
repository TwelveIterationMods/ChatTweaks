package net.blay09.mods.chattweaks.chat.emotes.twitch;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TwitchSubscriberEmotes {

    private static final Pattern DEFAULT_VALIDATION_PATTERN = Pattern.compile("[a-z0-9][a-z0-9]+[A-Z0-9].*");

    public TwitchSubscriberEmotes(String validationRegex) {
        JsonObject root = TwitchEmotesAPI.loadEmotes();
        if (root != null) {
            Pattern validationPattern;
            try {
                validationPattern = Pattern.compile(validationRegex);
            } catch (PatternSyntaxException e) {
                validationPattern = DEFAULT_VALIDATION_PATTERN;
            }

            Map<Integer, IEmoteGroup> groupMap = Maps.newHashMap();
            Matcher matcher = validationPattern.matcher("");
            JsonArray jsonArray = root.getAsJsonArray("emoticons");
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject entry = jsonArray.get(i).getAsJsonObject();
                int emoteSet = (!entry.has("emoticon_set") || entry.get("emoticon_set").isJsonNull()) ? TwitchEmotesAPI.EMOTESET_GLOBAL : entry.get("emoticon_set").getAsInt();
                if (emoteSet == TwitchEmotesAPI.EMOTESET_GLOBAL || emoteSet == TwitchEmotesAPI.EMOTESET_TURBO) {
                    continue;
                }

                String code = entry.get("code").getAsString();
                matcher.reset(code);
                if (matcher.matches()) {
                    String id = entry.get("id").getAsString();
                    IEmoteGroup group = groupMap.computeIfAbsent(emoteSet, s -> ChatTweaksAPI.registerEmoteGroup("Twitch-" + s));
                    String channel = TwitchEmotesAPI.getChannelForEmoteSet(emoteSet);
                    TwitchChannelEmoteData emoteData = new TwitchChannelEmoteData(id, channel);
                    IEmote emote = ChatTweaksAPI.registerEmote(code, TwitchChannelEmoteSource.INSTANCE, emoteData);
                    group.addEmote(emote);
                }
            }

            for (IEmoteGroup group : groupMap.values()) {
                if (group.getEmotes().size() < 5) {
                    EmoteRegistry.discardEmoteGroup(group);
                } else {
                    for (IEmote emote : group.getEmotes()) {
                        TwitchEmotesAPI.registerTwitchEmote(((TwitchChannelEmoteData) emote.getCustomData()).getId(), emote);
                    }
                }
            }
        }
    }

}
