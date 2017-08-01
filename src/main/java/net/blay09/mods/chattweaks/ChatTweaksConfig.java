package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.chat.emotes.LocalEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.BTTVChannelEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.BTTVEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.FFZChannelEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.FFZEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.TwitchGlobalEmotes;
import net.blay09.mods.chattweaks.chat.emotes.twitch.TwitchSubscriberEmotes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Configuration;

import java.io.*;

public class ChatTweaksConfig {

	private static Configuration config;
	public static boolean smallerEmotes = true;
	public static boolean alternateBackground = true;
	public static boolean emoteTabCompletion = false;
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
		emoteTabCompletion = config.getBoolean("Emote Tab Completion", "general", false, "Should emotes be considered in tab completion?");

		ChatManager.init();
	}

	public static void postInitLoad(Configuration config) {
		EmoteRegistry.reloadEmoticons();
		new Thread(() -> {
			EmoteRegistry.isLoading = true;
			try {
				if (config.getBoolean("Twitch Global Emotes", "emotes", true, "Should the Twitch Global emotes (ex. Kappa) be enabled?")) {
					new TwitchGlobalEmotes(
							config.getBoolean("Include Twitch Prime Emotes", "emotes", true, "Should Prime emotes (ex. KappaHD) be included with the Twitch Global Emotes?"),
							config.getBoolean("Include Twitch Smileys", "emotes", false, "Should smileys (ex. :-D) be included with the Twitch Global Emotes?")
					);
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load Twitch global emotes: ", e);
			}

			try {
				if (config.getBoolean("Twitch Subscriber Emotes", "emotes", true, "Should the Twitch Subscriber emotes (ex. geekPraise) be enabled?")) {
					new TwitchSubscriberEmotes(
							config.getString("Twitch Subscriber Emote Regex", "emotes", "[a-z0-9][a-z0-9]+[A-Z0-9].*", "The regex pattern to match for Twitch Subscriber Emotes to be included. By default includes all that follow prefixCode convention.")
					);
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load Twitch subscriber emotes: ", e);
			}

			try {
				if (config.getBoolean("BTTV Emotes", "emotes", true, "Should the BTTV emotes (ex. AngelThump) be enabled?")) {
					new BTTVEmotes();
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load BetterTTV emotes: ", e);
			}

			try {
				String[] bttvChannels = config.getStringList("BTTV Emote Channels", "emotes", new String[]{"ZeekDaGeek"}, "A list of channels to postInitLoad BTTV channel emotes from.");
				for (String channel : bttvChannels) {
					new BTTVChannelEmotes(channel);
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load BetterTTV channel emotes: ", e);
			}

			try {
				if (config.getBoolean("FFZ Emotes", "emotes", true, "Should the FrankerFaceZ emotes (ex. ZreknarF) be enabled?")) {
					new FFZEmotes();
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load FrankerFaceZ emotes: ", e);
			}

			try {
				String[] ffzChannels = config.getStringList("FFZ Emote Channels", "emotes", new String[]{"tehbasshunter"}, "A list of channels to load FrankerFaceZ channel emotes from.");
				for (String channel : ffzChannels) {
					new FFZChannelEmotes(channel);
				}
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load FrankerFaceZ channel emotes: ", e);
			}

			try {
				new LocalEmotes(new File(Minecraft.getMinecraft().mcDataDir, "chattweaks/emotes/"));
			} catch (Exception e) {
				ChatTweaks.logger.error("Failed to load local emotes: ", e);
			}
			EmoteRegistry.isLoading = false;
		}).run();

		ChatViewManager.load();
	}

}
