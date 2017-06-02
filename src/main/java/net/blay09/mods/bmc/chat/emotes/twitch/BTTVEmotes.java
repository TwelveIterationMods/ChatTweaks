package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksAPI;
import net.blay09.mods.bmc.chat.emotes.IEmote;
import net.blay09.mods.bmc.chat.emotes.IEmoteGroup;
import net.blay09.mods.bmc.chat.emotes.IEmoteLoader;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BTTVEmotes implements IEmoteLoader {

	private String urlTemplate;

	public BTTVEmotes() {
		JsonObject root = CachedAPI.loadCachedAPI("https://api.betterttv.net/2/emotes", "bttv_emotes.json", null);
		if(root != null) {
			if (!root.has("status") && root.get("status").getAsInt() != 200) {
				System.out.println("Failed to grab BTTV emotes.");
				return;
			}
			IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("BTTV");
			urlTemplate = root.get("urlTemplate").getAsString();
			JsonArray emotes = root.getAsJsonArray("emotes");
			for (int i = 0; i < emotes.size(); i++) {
				JsonObject entry = emotes.get(i).getAsJsonObject();
				String code = entry.get("code").getAsString();
				IEmote emote = ChatTweaksAPI.registerEmote(code, this);
				emote.setCustomData(entry.get("id").getAsString());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipBTTVEmotes"));
				emote.setImageCacheFile("bttv-" + entry.get("id").getAsString());
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + urlTemplate.replace("{{id}}", (String) emote.getCustomData()).replace("{{image}}", "1x")));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

}
