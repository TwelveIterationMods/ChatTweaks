package net.blay09.mods.chattweaks.chat.emotes.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteLoader;
import net.blay09.mods.chattweaks.balyware.CachedAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.URI;

public class BTTVChannelEmotes implements IEmoteLoader {

	private String urlTemplate;

	public BTTVChannelEmotes(String channelName) throws Exception {
		JsonObject root = CachedAPI.loadCachedAPI("https://api.betterttv.net/2/channels/" + channelName, "bttv_emotes_" + channelName + ".json", null);
		if(root != null) {
			if (!root.has("status") && root.get("status").getAsInt() != 200) {
				throw new Exception("Failed to grab BTTV channel emotes.");
			}
			IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("BTTV-" + channelName);
			urlTemplate = root.get("urlTemplate").getAsString();
			JsonArray emotes = root.getAsJsonArray("emotes");
			for (int i = 0; i < emotes.size(); i++) {
				JsonObject entry = emotes.get(i).getAsJsonObject();
				String code = entry.get("code").getAsString();
				IEmote emote = ChatTweaksAPI.registerEmote(code, this);
				emote.setCustomData(entry.get("id").getAsString());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + entry.get("channel").getAsString());
				emote.setImageCacheFile("bttv-" + entry.get("id").getAsString());
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) throws Exception {
		ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + urlTemplate.replace("{{id}}", (String) emote.getCustomData()).replace("{{image}}", "1x")));
	}

}
