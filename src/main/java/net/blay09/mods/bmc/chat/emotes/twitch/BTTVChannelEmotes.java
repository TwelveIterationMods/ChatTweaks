package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class BTTVChannelEmotes implements IEmoteLoader {

	private String urlTemplate;

	public BTTVChannelEmotes(String channelName) {
		JsonObject root = CachedAPI.loadCachedAPI("https://api.betterttv.net/2/channels/" + channelName, "bttv_emotes_" + channelName + ".json");
		if(root != null) {
			if (!root.has("status") && root.get("status").getAsInt() != 200) {
				System.out.println("Failed to grab BTTV channel emotes.");
				return;
			}
			IEmoteGroup group = BetterMinecraftChatAPI.registerEmoteGroup("BTTV-" + channelName);
			urlTemplate = root.get("urlTemplate").getAsString();
			JsonArray emotes = root.getAsJsonArray("emotes");
			for (int i = 0; i < emotes.size(); i++) {
				JsonObject entry = emotes.get(i).getAsJsonObject();
				String code = entry.get("code").getAsString();
				IEmote emote = BetterMinecraftChatAPI.registerEmote(code, this);
				emote.setCustomData(entry.get("id").getAsString());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + entry.get("channel").getAsString());
				emote.setImageCacheFile("bttv-" + entry.get("id").getAsString());
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			BetterMinecraftChatAPI.loadEmoteImage(emote, new URI("https:" + urlTemplate.replace("{{id}}", (String) emote.getCustomData()).replace("{{image}}", "1x")));
		} catch (URISyntaxException | MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
