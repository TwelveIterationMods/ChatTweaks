package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class TwitchGlobalEmotes implements IEmoteLoader {

	private static final String URL_TEMPLATE = "https://static-cdn.jtvnw.net/emoticons/v1/{{id}}/1.0";

	public TwitchGlobalEmotes(boolean includeTurbo, boolean includeSmileys) {
		JsonObject root = includeTurbo ? TwitchAPI.loadEmotes(TwitchAPI.EMOTESET_GLOBAL, TwitchAPI.EMOTESET_TURBO) : TwitchAPI.loadEmotes(TwitchAPI.EMOTESET_GLOBAL);
		if(root != null) {
			loadEmotes(root.getAsJsonObject("emoticon_sets").getAsJsonArray(String.valueOf(TwitchAPI.EMOTESET_GLOBAL)), TextFormatting.GRAY + I18n.format(BetterMinecraftChat.MOD_ID + ":gui.chat.tooltipTwitchEmotes"), includeSmileys, BetterMinecraftChatAPI.registerEmoteGroup("TwitchGlobal"));
			loadEmotes(root.getAsJsonObject("emoticon_sets").getAsJsonArray(String.valueOf(TwitchAPI.EMOTESET_TURBO)), TextFormatting.GRAY + I18n.format(BetterMinecraftChat.MOD_ID + ":gui.chat.tooltipTwitchTurboEmotes"), includeSmileys, BetterMinecraftChatAPI.registerEmoteGroup("TwitchTurbo"));
		}
	}

	private void loadEmotes(JsonArray jsonArray, String tooltip, boolean includeSmileys, IEmoteGroup group) {
		for(int i = 0; i < jsonArray.size(); i++) {
			JsonObject entry = jsonArray.get(i).getAsJsonObject();
			String code = entry.get("code").getAsString();
			IEmote emote;
			if(code.matches(".*\\p{Punct}.*")) {
				if(!includeSmileys) {
					continue;
				}
				code = code.replace("\\\\", "\\");
				code = code.replace("&lt\\;", "<");
				code = code.replace("&gt\\;", ">");
				emote = BetterMinecraftChatAPI.registerRegexEmote(code, this);
			} else {
				emote = BetterMinecraftChatAPI.registerEmote(code, this);
			}
			emote.setCustomData(entry.get("id").getAsInt());
			emote.addTooltip(tooltip);
			emote.setImageCacheFile("twitch-" + entry.get("id").getAsInt());
			group.addEmote(emote);
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			BetterMinecraftChatAPI.loadEmoteImage(emote, new URI(URL_TEMPLATE.replace("{{id}}", String.valueOf(emote.getCustomData()))));
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
