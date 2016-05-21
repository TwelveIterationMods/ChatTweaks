package net.blay09.mods.bmc.chat.badges;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IntHashMap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class PatronBadges {

	private static final IntHashMap<NameBadge> badges = new IntHashMap<>();
	private static final Map<String, NameBadge> playerMap = Maps.newHashMap();

	public static void init() {
		JsonObject root = CachedAPI.loadCachedAPI("http://balyware.com/control-panel/api/badges.php", "patron_badges.json");
		if(root != null) {
			String urlTemplate = "http:" + root.get("url_template").getAsString();
			JsonArray jsonArray = root.getAsJsonArray("badges");
			for(int i = 0; i < jsonArray.size(); i++) {
				JsonObject entry = jsonArray.get(i).getAsJsonObject();
				int id = entry.get("id").getAsInt();
				IChatRenderable image = null;
				try {
					image = BetterMinecraftChatAPI.loadImage(new URI(urlTemplate.replace("{{id}}", String.valueOf(id))), new File(Minecraft.getMinecraft().mcDataDir, "/bmc/cache/badge-" + id));
				} catch (MalformedURLException | URISyntaxException e) {
					e.printStackTrace();
				}
				if(image != null) {
					image.setScale(0.3f);
					NameBadge nameBadge = new NameBadge(id, entry.get("name").getAsString(), image);
					JsonArray players = entry.getAsJsonArray("players");
					for(int j = 0; j < players.size(); j++) {
						String playerName = players.get(j).getAsString();
						nameBadge.addPlayer(playerName);
						playerMap.put(playerName, nameBadge);
					}
					badges.addKey(id, nameBadge);
				}
			}
		}
	}

	public static NameBadge getBadgeForPlayer(String name) {
		return playerMap.get(name);
	}

}
