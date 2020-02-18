package net.blay09.mods.chattweaks.chat.emotes.bttv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.balyware.CachedAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

public class BTTVChannelEmotes {

    public BTTVChannelEmotes(String channelName) throws Exception {
        JsonObject root = CachedAPI.loadCachedAPI("https://api.betterttv.net/2/channels/" + channelName, "bttv_emotes_" + channelName + ".json", null);
        if (root != null) {
            if (!root.has("urlTemplate") || !root.has("emotes")) {
                throw new Exception("Failed to grab BTTV channel emotes.");
            }
            IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("BTTV-" + channelName);
            BTTVChannelEmoteSource.INSTANCE.setUrlTemplate(root.get("urlTemplate").getAsString());
            JsonArray emotes = root.getAsJsonArray("emotes");
            for (int i = 0; i < emotes.size(); i++) {
                JsonObject entry = emotes.get(i).getAsJsonObject();
                String code = entry.get("code").getAsString();
                BTTVChannelEmoteData emoteData = new BTTVChannelEmoteData(entry.get("id").getAsString(), entry.get("channel").getAsString());
                IEmote emote = ChatTweaksAPI.registerEmote(code, BTTVChannelEmoteSource.INSTANCE, emoteData);
                group.addEmote(emote);
            }
        }
    }

}
