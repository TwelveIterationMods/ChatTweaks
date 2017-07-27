package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class FFZChannelEmotes implements IEmoteLoader {

	public FFZChannelEmotes(String channelName) {
		try {
			URL apiURL = new URL("https://api.frankerfacez.com/v1/room/" + channelName);
			InputStreamReader reader = new InputStreamReader(apiURL.openStream());
			Gson gson = new Gson();
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			if(root == null) {
				System.out.println("Failed to grab FrankerFaceZ channel emotes for " + channelName);
				return;
			}
			JsonObject room = root.getAsJsonObject("room");
			String displayName = room.get("display_name").getAsString();
			IEmoteGroup group = BetterMinecraftChatAPI.registerEmoteGroup("FFZ-" + channelName);
			int setId = room.get("set").getAsInt();
			JsonObject set = root.getAsJsonObject("sets").getAsJsonObject(String.valueOf(setId));
			JsonArray emoticons = set.getAsJsonArray("emoticons");
			for(int j = 0; j < emoticons.size(); j++) {
				JsonObject emoticonObject = emoticons.get(j).getAsJsonObject();
				String code = emoticonObject.get("name").getAsString();
				IEmote emote = BetterMinecraftChatAPI.registerEmote(code, this);
				emote.setCustomData(emoticonObject.getAsJsonObject("urls").get("1").getAsString());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(BetterMinecraftChat.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + displayName);
				emote.setImageCacheFile("ffz-" + emoticonObject.get("id").getAsString());
				group.addEmote(emote);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			BetterMinecraftChatAPI.loadEmoteImage(emote, new URI("https:" + emote.getCustomData()));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

}
