package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.minecraft.util.IntHashMap;

import java.util.Map;

public class TwitchAPI {

	public static final int EMOTESET_GLOBAL = 0;
	public static final int EMOTESET_TURBO = 457;

	private static final IntHashMap<String> emoteSets = new IntHashMap<>();
	private static final IntHashMap<IEmote> twitchEmotes = new IntHashMap<>();

	public static void init() {
		JsonObject object = CachedAPI.loadCachedAPI("https://twitchemotes.com/api_cache/v2/sets.json", "twitch_emotesets.json");
		if(object != null) {
			JsonObject sets = object.get("sets").getAsJsonObject();
			for(Map.Entry<String, JsonElement> entry : sets.entrySet()) {
				emoteSets.addKey(Integer.parseInt(entry.getKey()), entry.getValue().getAsString());
			}
		}
	}

	public static String getChannelForEmoteSet(int emoteSet) {
		return emoteSets.lookup(emoteSet);
	}

	public static JsonObject loadEmotes(int... emotesets) {
		StringBuilder sb = new StringBuilder();
		for (int emoteset : emotesets) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			sb.append(emoteset);
		}
		String url = "https://api.twitch.tv/kraken/chat/emoticon_images";
		if(emotesets.length > 0) {
			url += "?emotesets=" + sb.toString();
		}
		return CachedAPI.loadCachedAPI(url, "twitch_emotes" + (sb.length() > 0 ? "-" + sb.toString() : "") + ".json");
	}

	public static void registerTwitchEmote(int id, IEmote emote) {
		twitchEmotes.addKey(id, emote);
	}

	public static IEmote getEmoteById(int id) {
		return twitchEmotes.lookup(id);
	}
}
