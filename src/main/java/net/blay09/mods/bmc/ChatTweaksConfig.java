package net.blay09.mods.bmc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.bmc.api.chat.IChatChannel;
import net.blay09.mods.bmc.api.chat.MessageStyle;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.emotes.DefaultEmotes;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.chat.emotes.LocalEmotes;
import net.blay09.mods.bmc.chat.emotes.PatronEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.BTTVChannelEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.BTTVEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchGlobalEmotes;
import net.blay09.mods.bmc.chat.emotes.twitch.TwitchSubscriberEmotes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

import java.io.*;

public class ChatTweaksConfig {

	private static Configuration config;
	public static boolean smallerEmotes = false;
	public static int backgroundColor1;
	public static int backgroundColor2;
	public static int backgroundColorHighlight;
	public static boolean highlightName;
	public static String[] highlightStrings;
	public static boolean emoteTabCompletion;
	public static boolean randomNameColors;
	public static boolean enableNameBadges;

	public static void preInitLoad(Configuration config) {
		ChatTweaksConfig.config = config;
		backgroundColor1 = ChatTweaks.colorFromHex(config.getString("Background Color 1", "theme", "000000", "The background color to use for even line numbers in HEX."));
		backgroundColor2 = ChatTweaks.colorFromHex(config.getString("Background Color 2", "theme", "111111", "The background color to use for uneven line numbers in HEX."));
		backgroundColorHighlight = ChatTweaks.colorFromHex(config.getString("Highlight Color", "theme", "FF0000", "The background color to use for highlighted lines in HEX."));
		highlightName = config.getBoolean("Highlight Name", "highlights", true, "If set to true, mentions of your Minecraft IGN will be highlighted in chat.");
		highlightStrings = config.getStringList("Highlighted Words", "highlights", new String[0], "List of words that are highlighted in chat.");
		emoteTabCompletion = config.getBoolean("Tab Completion for Emotes", "general", false, "Should Tab completion be enabled for emotes?");
		smallerEmotes = config.getBoolean("Smaller Emotes", "general", false, "Should emotes be scaled down to perfectly fit into one line?");
		randomNameColors = config.getBoolean("Random Name Colors", "general", false, "Should players be assigned random name colors? They're only visual and will not be synchronized with other players.");
		enableNameBadges = config.getBoolean("Enable Name Badges", "general", true, "Should name badges for supporters, contributors and developers of BalyWare be enabled?");
	}

	public static void postInitLoad(Configuration config) {
		EmoteRegistry.reloadEmoticons();

		if(config.getBoolean("Default Emotes", "emotes", true, "Should the default emotes (ex. eiraMeow) be enabled?")) {
			new DefaultEmotes("eiraArr", "eiraCri", "eiraCute", "eiraFufu", "eiraLewd", "eiraMeow", "eiraPraise", "eiraRage", "eiraScared", "eiraYawn");
		}

		if(config.getBoolean("Twitch Global Emotes", "emotes", true, "Should the Twitch Global emotes (ex. Kappa) be enabled?")) {
			new TwitchGlobalEmotes(
					config.getBoolean("Include Twitch Turbo Emotes", "emotes", true, "Should Turbo emotes (ex. KappaHD) be included with the Twitch Global Emotes?"),
					config.getBoolean("Include Twitch Smileys", "emotes", true, "Should smileys (ex. :-D) be included with the Twitch Global Emotes?")
			);
		}

		if(config.getBoolean("Twitch Subscriber Emotes", "emotes", true, "Should the Twitch Subscriber emotes (ex. geekPraise) be enabled?")) {
			new TwitchSubscriberEmotes(
					config.getString("Twitch Subscriber Emote Regex", "emotes", "[a-z0-9][a-z0-9]+[A-Z0-9].*", "The regex pattern to match for Twitch Subscriber Emotes to be included. By default includes all that follow prefixCode convention.")
			);
		}

		if(config.getBoolean("BTTV Emotes", "emotes", true, "Should the BTTV emotes (ex. AngelThump) be enabled?")) {
			new BTTVEmotes();
		}

		String[] bttvChannels = config.getStringList("BTTV Emote Channels", "emotes", new String[] { "ZeekDaGeek" }, "A list of channels to postInitLoad BTTV channel emotes from.");
		for(String channel : bttvChannels) {
			new BTTVChannelEmotes(channel);
		}

		if(config.getBoolean("Patron Emotes", "emotes", true, "Should the user-submitted emotes from Patreon supporters be enabled (note: they undergo an approval process first, so they're safe).")) {
			new PatronEmotes();
		}

		new LocalEmotes(new File(Minecraft.getMinecraft().mcDataDir, "bmc/emotes/"));

		Gson gson = new Gson();
		try(FileReader reader = new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "config/BetterMinecraftChat/channels.json"))) {
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			JsonArray channels = root.getAsJsonArray("channels");
			for(int i = 0; i < channels.size(); i++) {
				JsonObject channel = channels.get(i).getAsJsonObject();
				ChatChannel chatChannel = ChatChannel.fromJson(channel);
				if(chatChannel != null) {
					ChatTweaks.getChatHandler().addChannel(chatChannel);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(ChatTweaks.getChatHandler().getChannels().isEmpty()) {
			createDefaultChannels();
		}

		IChatChannel defaultChannel = ChatTweaks.getChatHandler().getNextChatChannel(null, false);
		if(defaultChannel == null) {
			defaultChannel = ChatTweaks.getChatHandler().getChannels().get(0);
		}
		ChatTweaks.getChatHandler().setActiveChannel((ChatChannel) defaultChannel);
	}

	private static void createDefaultChannels() {
		ChatChannel defaultChannel = new ChatChannel("*");
		defaultChannel.setShowTimestamps(true);
		ChatTweaks.getChatHandler().addChannel(defaultChannel);

		ChatChannel bedSpamChannel = new ChatChannel("Bed Message");
		bedSpamChannel.setFilterPattern("You can only sleep at night");
		bedSpamChannel.setFormat("~c$0");
		bedSpamChannel.setMessageStyle(MessageStyle.Bottom);
		bedSpamChannel.setExclusive(true);
		ChatTweaks.getChatHandler().addChannel(bedSpamChannel);

		ChatChannel commandChannel = new ChatChannel("Common Commands");
		commandChannel.setFilterPattern("(Set the time to [0-9]+|Toggled downfall|Given \\[.+\\] \\* [0-9]+ to .+)");
		commandChannel.setMessageStyle(MessageStyle.Side);
		commandChannel.setExclusive(true);
		ChatTweaks.getChatHandler().addChannel(commandChannel);
	}

	public static void saveChannels() {
		Gson gson = new Gson();
		try(FileWriter writer = new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/BetterMinecraftChat/channels.json"))) {
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("  ");
			JsonObject root = new JsonObject();
			JsonArray channels = new JsonArray();
			for(ChatChannel channel : ChatTweaks.getChatHandler().getChannels()) {
				if(!channel.isTemporary()) {
					channels.add(channel.toJson());
				}
			}
			root.add("channels", channels);
			gson.toJson(root, jsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
