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

public class FFZEmotes implements IEmoteLoader {

	public FFZEmotes() throws Exception {
		URL apiURL = new URL("http://api.frankerfacez.com/v1/set/global");
		InputStreamReader reader = new InputStreamReader(apiURL.openStream());
		Gson gson = new Gson();
		JsonObject root = gson.fromJson(reader, JsonObject.class);
		if(root == null) {
			throw new Exception("Failed to grab FrankerFaceZ emotes");
		}
		IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("FFZ");
		JsonArray defaultSets = root.getAsJsonArray("default_sets");
		JsonObject sets = root.getAsJsonObject("sets");
		for(int i = 0; i < defaultSets.size(); i++) {
			int setId = defaultSets.get(i).getAsInt();
			JsonObject set = sets.getAsJsonObject(String.valueOf(setId));
			JsonArray emoticons = set.getAsJsonArray("emoticons");
			for(int j = 0; j < emoticons.size(); j++) {
				JsonObject emoticonObject = emoticons.get(j).getAsJsonObject();
				String code = emoticonObject.get("name").getAsString();
				IEmote emote = ChatTweaksAPI.registerEmote(code, this);
				emote.setCustomData(emoticonObject.getAsJsonObject("urls").get("1").getAsString());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipFFZEmotes"));
				emote.setImageCacheFile("ffz-" + emoticonObject.get("id").getAsString());
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) throws Exception {
		ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + emote.getCustomData()));
	}

	@Override
	public boolean isCommonEmote(String name) {
		return true;
	}
}
