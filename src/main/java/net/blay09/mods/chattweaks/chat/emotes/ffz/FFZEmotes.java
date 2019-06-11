package net.blay09.mods.chattweaks.chat.emotes.ffz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

import java.io.InputStreamReader;
import java.net.URL;

public class FFZEmotes {

    public FFZEmotes() throws Exception {
        URL apiURL = new URL("https://api.frankerfacez.com/v1/set/global");
        InputStreamReader reader = new InputStreamReader(apiURL.openStream());
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(reader, JsonObject.class);
        if (root == null) {
            throw new Exception("Failed to grab FrankerFaceZ emotes");
        }
        IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("FFZ");
        JsonArray defaultSets = root.getAsJsonArray("default_sets");
        JsonObject sets = root.getAsJsonObject("sets");
        for (int i = 0; i < defaultSets.size(); i++) {
            int setId = defaultSets.get(i).getAsInt();
            JsonObject set = sets.getAsJsonObject(String.valueOf(setId));
            JsonArray emoticons = set.getAsJsonArray("emoticons");
            for (int j = 0; j < emoticons.size(); j++) {
                JsonObject emoticonObject = emoticons.get(j).getAsJsonObject();
                String code = emoticonObject.get("name").getAsString();
                FFZEmoteData emoteData = new FFZEmoteData(emoticonObject.get("id").getAsString(), emoticonObject.getAsJsonObject("urls").get("1").getAsString());
                IEmote emote = ChatTweaksAPI.registerEmote(code, FFZEmoteSource.INSTANCE, emoteData);
                group.addEmote(emote);
            }
        }
    }

}
