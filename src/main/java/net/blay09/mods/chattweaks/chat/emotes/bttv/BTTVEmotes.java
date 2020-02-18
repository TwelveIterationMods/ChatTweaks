package net.blay09.mods.chattweaks.chat.emotes.bttv;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.balyware.CachedAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

public class BTTVEmotes {

    public BTTVEmotes() throws Exception {
        JsonObject root = CachedAPI.loadCachedAPI("https://api.betterttv.net/2/emotes", "bttv_emotes.json", null);
        if (root != null) {
            if (!root.has("urlTemplate") || !root.has("emotes")) {
                throw new Exception("Failed to grab BTTV emotes.");
            }
            IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("BTTV");

            BTTVEmoteSource.INSTANCE.setUrlTemplate(root.get("urlTemplate").getAsString());

            JsonArray emotes = root.getAsJsonArray("emotes");
            for (int i = 0; i < emotes.size(); i++) {
                JsonObject entry = emotes.get(i).getAsJsonObject();
                String code = entry.get("code").getAsString();
                IEmote emote = ChatTweaksAPI.registerEmote(code, BTTVEmoteSource.INSTANCE, entry.get("id").getAsString());
                group.addEmote(emote);
            }
        }
    }
}
