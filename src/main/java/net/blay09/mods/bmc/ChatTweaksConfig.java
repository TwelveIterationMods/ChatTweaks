package net.blay09.mods.bmc;

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
	public static boolean smallerEmotes = true;
	public static boolean alternateBackground = true;
	public static int backgroundColor1;
	public static int backgroundColor2;
	public static int backgroundColorHighlight;
	public static int lineSpacing;
	public static boolean highlightName;
	public static String[] highlightStrings;

	public static void preInitLoad(Configuration config) {
		ChatTweaksConfig.config = config;
		backgroundColor1 = ChatTweaks.colorFromHex(config.getString("Background Color 1", "theme", "000000", "The background color to use for even line numbers in HEX."));
		backgroundColor2 = ChatTweaks.colorFromHex(config.getString("Background Color 2", "theme", "111111", "The background color to use for uneven line numbers in HEX (if enabled)."));
		backgroundColorHighlight = ChatTweaks.colorFromHex(config.getString("Highlight Color", "theme", "FF0000", "The background color to use for highlighted lines in HEX."));
		highlightName = config.getBoolean("Highlight Name", "highlights", true, "If set to true, mentions of your Minecraft IGN will be highlighted in chat.");
		highlightStrings = config.getStringList("Highlighted Words", "highlights", new String[0], "List of words that are highlighted in chat.");
		alternateBackground = config.getBoolean("Alternate Background Color", "general", true, "Should uneven lines alternate their background color for easier reading?");
		smallerEmotes = config.getBoolean("Smaller Emotes", "general", false, "Should emotes be scaled down to perfectly fit into one line?");

		ChatManager.init();
	}

	public static void postInitLoad(Configuration config) {
		EmoteRegistry.reloadEmoticons();

		if (config.getBoolean("Default Emotes", "emotes", true, "Should the default emotes (ex. eiraMeow) be enabled?")) {
			new DefaultEmotes("eiraArr", "eiraCri", "eiraCute", "eiraFufu", "eiraLewd", "eiraMeow", "eiraPraise", "eiraRage", "eiraScared", "eiraYawn");
		}

		if (config.getBoolean("Twitch Global Emotes", "emotes", true, "Should the Twitch Global emotes (ex. Kappa) be enabled?")) {
			new TwitchGlobalEmotes(
					config.getBoolean("Include Twitch Turbo Emotes", "emotes", true, "Should Turbo emotes (ex. KappaHD) be included with the Twitch Global Emotes?"),
					config.getBoolean("Include Twitch Smileys", "emotes", false, "Should smileys (ex. :-D) be included with the Twitch Global Emotes?")
			);
		}

		if (config.getBoolean("Twitch Subscriber Emotes", "emotes", true, "Should the Twitch Subscriber emotes (ex. geekPraise) be enabled?")) {
			new TwitchSubscriberEmotes(
					config.getString("Twitch Subscriber Emote Regex", "emotes", "[a-z0-9][a-z0-9]+[A-Z0-9].*", "The regex pattern to match for Twitch Subscriber Emotes to be included. By default includes all that follow prefixCode convention.")
			);
		}

		if (config.getBoolean("BTTV Emotes", "emotes", true, "Should the BTTV emotes (ex. AngelThump) be enabled?")) {
			new BTTVEmotes();
		}

		String[] bttvChannels = config.getStringList("BTTV Emote Channels", "emotes", new String[]{"ZeekDaGeek"}, "A list of channels to postInitLoad BTTV channel emotes from.");
		for (String channel : bttvChannels) {
			new BTTVChannelEmotes(channel);
		}

		if (config.getBoolean("Patron Emotes", "emotes", true, "Should the user-submitted emotes from Patreon supporters be enabled (note: they undergo an approval process first, so they're safe).")) {
			new PatronEmotes();
		}

		new LocalEmotes(new File(Minecraft.getMinecraft().mcDataDir, "bmc/emotes/"));

		ChatViewManager.load();
	}

}
