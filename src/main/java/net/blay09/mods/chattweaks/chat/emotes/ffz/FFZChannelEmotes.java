package net.blay09.mods.chattweaks.chat.emotes.ffz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;

public class FFZChannelEmotes {

	public FFZChannelEmotes(String channelName) throws Exception {
		try {
			URL apiURL = new URL("https://api.frankerfacez.com/v1/room/" + channelName);
			InputStreamReader reader = new InputStreamReader(apiURL.openStream());
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			if (root == null) {
				throw new Exception("Failed to grab FrankerFaceZ channel emotes for " + channelName);
			}
			JsonObject room = root.getAsJsonObject("room");
			String displayName = room.get("display_name").getAsString();
			IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("FFZ-" + channelName);
			int setId = room.get("set").getAsInt();
			JsonObject set = root.getAsJsonObject("sets").getAsJsonObject(String.valueOf(setId));
			JsonArray emoticons = set.getAsJsonArray("emoticons");
			for (int j = 0; j < emoticons.size(); j++) {
				JsonObject emoticonObject = emoticons.get(j).getAsJsonObject();
				String code = emoticonObject.get("name").getAsString();
				FFZChannelEmoteData emoteData = new FFZChannelEmoteData(emoticonObject.get("id").getAsString(), emoticonObject.getAsJsonObject("urls").get("1").getAsString(), displayName);
				IEmote emote = ChatTweaksAPI.registerEmote(code, FFZChannelEmoteSource.INSTANCE, emoteData);
				group.addEmote(emote);
			}
		} catch (FileNotFoundException ignored) {
			// Ignore this, it's thrown when a channel doesn't have FFZ emotes
		}
	}

}
