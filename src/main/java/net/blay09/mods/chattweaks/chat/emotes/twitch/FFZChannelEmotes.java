package net.blay09.mods.chattweaks.chat.emotes.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class FFZChannelEmotes implements IEmoteLoader {

	public FFZChannelEmotes(String channelName) throws Exception {
		URL apiURL = new URL("https://api.frankerfacez.com/v1/room/" + channelName);
		InputStreamReader reader = new InputStreamReader(apiURL.openStream());
		Gson gson = new Gson();
		JsonObject root = gson.fromJson(reader, JsonObject.class);
		if(root == null) {
			throw new Exception("Failed to grab FrankerFaceZ channel emotes for " + channelName);
		}
		JsonObject room = root.getAsJsonObject("room");
		String displayName = room.get("display_name").getAsString();
		IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("FFZ-" + channelName);
		int setId = room.get("set").getAsInt();
		JsonObject set = root.getAsJsonObject("sets").getAsJsonObject(String.valueOf(setId));
		JsonArray emoticons = set.getAsJsonArray("emoticons");
		for(int j = 0; j < emoticons.size(); j++) {
			JsonObject emoticonObject = emoticons.get(j).getAsJsonObject();
			String code = emoticonObject.get("name").getAsString();
			IEmote emote = ChatTweaksAPI.registerEmote(code, this);
			emote.setCustomData(emoticonObject.getAsJsonObject("urls").get("1").getAsString());
			emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + displayName);
			emote.setImageCacheFile("ffz-" + emoticonObject.get("id").getAsString());
			group.addEmote(emote);
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) throws Exception {
		ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + emote.getCustomData()));
	}

}
