package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.blay09.mods.bmc.balyware.CachedAPI;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class TwitchBadge {

	private static final Map<String, TwitchBadge> twitchBadges = Maps.newHashMap();

	private final IChatRenderable chatRenderable;
	private final ITooltipProvider tooltipProvider;

	public TwitchBadge(IChatRenderable chatRenderable, ITooltipProvider tooltipProvider) {
		this.chatRenderable = chatRenderable;
		this.tooltipProvider = tooltipProvider;
	}

	public IChatRenderable getChatRenderable() {
		return chatRenderable;
	}

	public ITooltipProvider getTooltipProvider() {
		return tooltipProvider;
	}

	public static TwitchBadge getSubscriberBadge(String channel) {
		TwitchBadge badge = twitchBadges.get(channel);
		if(badge == null) {
			JsonObject object = CachedAPI.loadCachedAPI("https://api.twitch.tv/kraken/chat/" + channel + "/badges", "badges_" + channel);
			JsonElement element = object.get("subscriber");
			if(!element.isJsonNull()) {
				try {
					IChatRenderable chatRenderable = BetterMinecraftChatAPI.loadImage(new URI(element.getAsJsonObject().get("image").getAsString()), new File(Minecraft.getMinecraft().mcDataDir, "bmc/cache/badge_" + channel));
					chatRenderable.setScale(0.45f);
					badge = new TwitchBadge(chatRenderable, ITooltipProvider.EMPTY);
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
			twitchBadges.put(channel, badge);
		}
		return badge;
	}

	public static void loadInbuiltBadge(String name) {
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(TwitchIntegration.MOD_ID, "badges/badge_" + name + ".png"));
			IChatRenderable chatRenderable = BetterMinecraftChatAPI.loadImage(resource.getInputStream(), null);
			chatRenderable.setScale(0.45f);
			twitchBadges.put(name, new TwitchBadge(chatRenderable, ITooltipProvider.EMPTY));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static TwitchBadge getBadge(String name) {
		return twitchBadges.get(name);
	}
}
