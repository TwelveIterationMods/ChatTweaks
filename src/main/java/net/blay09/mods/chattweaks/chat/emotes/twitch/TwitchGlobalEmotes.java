package net.blay09.mods.chattweaks.chat.emotes.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

public class TwitchGlobalEmotes {

    public TwitchGlobalEmotes(boolean includeTurbo) {
        JsonObject root = includeTurbo ? TwitchEmotesAPI.loadEmotes(TwitchEmotesAPI.EMOTESET_GLOBAL, TwitchEmotesAPI.EMOTESET_TURBO) : TwitchEmotesAPI.loadEmotes(TwitchEmotesAPI.EMOTESET_GLOBAL);
        if (root != null) {
            JsonObject emoticonSets = root.getAsJsonObject("emoticon_sets");
            loadEmotes(emoticonSets.getAsJsonArray(String.valueOf(TwitchEmotesAPI.EMOTESET_GLOBAL)), ChatTweaksAPI.registerEmoteGroup("TwitchGlobal"));
            loadEmotes(emoticonSets.getAsJsonArray(String.valueOf(TwitchEmotesAPI.EMOTESET_TURBO)), ChatTweaksAPI.registerEmoteGroup("TwitchTurbo"));
        }
    }

    private void loadEmotes(JsonArray jsonArray, IEmoteGroup group) {
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject entry = jsonArray.get(i).getAsJsonObject();
            String id = entry.get("id").getAsString();
            String code = entry.get("code").getAsString();
            IEmote<?> emote = ChatTweaksAPI.registerEmote(code, TwitchGlobalEmoteSource.INSTANCE, id);
            group.addEmote(emote);

            TwitchEmotesAPI.registerTwitchEmote(id, emote);
        }
    }

}
