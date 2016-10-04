package net.blay09.mods.bmc.chat.emotes.twitch;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksAPI;
import net.blay09.mods.bmc.chat.emotes.IEmote;
import net.blay09.mods.bmc.chat.emotes.IEmoteGroup;
import net.blay09.mods.bmc.chat.emotes.IEmoteLoader;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class TwitchSubscriberEmotes implements IEmoteLoader {

	private static final Pattern DEFAULT_VALIDATION_PATTERN = Pattern.compile("[a-z0-9][a-z0-9]+[A-Z0-9].*");
	private static final String URL_TEMPLATE = "https://static-cdn.jtvnw.net/emoticons/v1/{{id}}/1.0";

	public TwitchSubscriberEmotes(String validationRegex) {
		JsonObject root = TwitchAPI.loadEmotes();
		if(root != null) {
			Pattern validationPattern;
			try {
				validationPattern = Pattern.compile(validationRegex);
			} catch (PatternSyntaxException e) {
				validationPattern = DEFAULT_VALIDATION_PATTERN;
			}
			Map<Integer, IEmoteGroup> groupMap = Maps.newHashMap();
			Matcher matcher = validationPattern.matcher("");
			JsonArray jsonArray = root.getAsJsonArray("emoticons");
			for(int i = 0; i < jsonArray.size(); i++) {
				JsonObject entry = jsonArray.get(i).getAsJsonObject();
				int emoteSet = (!entry.has("emoticon_set") || entry.get("emoticon_set").isJsonNull()) ? TwitchAPI.EMOTESET_GLOBAL : entry.get("emoticon_set").getAsInt();
				if(emoteSet == TwitchAPI.EMOTESET_GLOBAL || emoteSet == TwitchAPI.EMOTESET_TURBO) {
					continue;
				}
				String code = entry.get("code").getAsString();
				matcher.reset(code);
				if(matcher.matches()) {
					int id = entry.get("id").getAsInt();
					IEmoteGroup group = groupMap.get(emoteSet);
					if(group == null) {
						group = ChatTweaksAPI.registerEmoteGroup("Twitch-" + emoteSet);
						groupMap.put(emoteSet, group);
					}
					IEmote emote = ChatTweaksAPI.registerEmote(code, this);
					emote.setCustomData(id);
					String channel = TwitchAPI.getChannelForEmoteSet(emoteSet);
					if(channel != null) {
						emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + channel);
					}
					emote.setImageCacheFile("twitch-" + id);
					group.addEmote(emote);
					TwitchAPI.registerTwitchEmote(id, emote);
				}
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			ChatTweaksAPI.loadEmoteImage(emote, new URI(URL_TEMPLATE.replace("{{id}}", String.valueOf(emote.getCustomData()))));
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
	}

}
