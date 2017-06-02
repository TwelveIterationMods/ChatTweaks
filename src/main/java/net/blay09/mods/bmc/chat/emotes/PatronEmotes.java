package net.blay09.mods.bmc.chat.emotes;

import com.google.gson.*;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksAPI;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PatronEmotes implements IEmoteLoader {

	private String urlTemplate;

	public PatronEmotes() {
		JsonObject root = CachedAPI.loadCachedAPI("http://blay09.net/mods/control-panel/api/emotes.php", "patron_emotes.json", null);
		if(root != null) {
			IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("Patreon");
			urlTemplate = "http:" + root.get("url_template").getAsString();
			JsonArray jsonArray = root.getAsJsonArray("emotes");
			for(int i = 0; i < jsonArray.size(); i++) {
				JsonObject entry = jsonArray.get(i).getAsJsonObject();
				IEmote emote = ChatTweaksAPI.registerEmote(entry.get("code").getAsString(), this);
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
				ChatTweaksAPI.loadEmoteImage(emote, new URI(urlTemplate.replace("{{id}}", String.valueOf(emote.getCustomData()))));
			} catch (URISyntaxException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}
