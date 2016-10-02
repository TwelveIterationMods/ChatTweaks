package net.blay09.mods.bmc.chat.emotes;

import com.google.gson.*;
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

public class PatronEmotes implements IEmoteLoader {

	private String urlTemplate;

	public PatronEmotes() {
		JsonObject root = CachedAPI.loadCachedAPI("http://balyware.com/control-panel/api/emotes.php", "patron_emotes.json");
		if(root != null) {
			IEmoteGroup group = BetterMinecraftChatAPI.registerEmoteGroup("Patreon");
			urlTemplate = "http:" + root.get("url_template").getAsString();
			JsonArray jsonArray = root.getAsJsonArray("emotes");
			for(int i = 0; i < jsonArray.size(); i++) {
				JsonObject entry = jsonArray.get(i).getAsJsonObject();
				IEmote emote = BetterMinecraftChatAPI.registerEmote(entry.get("code").getAsString(), this);
				emote.setCustomData(entry.get("id").getAsInt());
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmotePatron") + entry.get("owner").getAsString());
				emote.setImageCacheFile("patron-" + entry.get("id").getAsInt());
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		if(urlTemplate != null) {
			try {
				BetterMinecraftChatAPI.loadEmoteImage(emote, new URI(urlTemplate.replace("{{id}}", String.valueOf(emote.getCustomData()))));
			} catch (URISyntaxException | MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

}
