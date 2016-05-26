package net.blay09.mods.bmc.integration.twitch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchChannel;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TwitchIntegrationConfig {

	private static File configFile;

	public static boolean useAnonymousLogin;
	public static boolean showWhispers;
	public static String singleMessageFormat;
	public static String multiMessageFormat;
	public static String singleActionFormat;
	public static String multiActionFormat;
	public static String whisperMessageFormat;
	public static String whisperActionFormat;

	public static void load(File configFile) {
		TwitchIntegrationConfig.configFile = configFile;
		Gson gson = new Gson();
		try (FileReader reader = new FileReader(configFile)) {
			TwitchIntegrationConfig.load(gson.fromJson(reader, JsonObject.class), TwitchIntegration.getTwitchManager());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		Gson gson = new Gson();
		try (JsonWriter writer = new JsonWriter(new FileWriter(configFile))) {
			writer.setIndent("  ");
			gson.toJson(save(new JsonObject(), TwitchIntegration.getTwitchManager()), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void load(JsonObject jsonRoot, TwitchManager twitchManager) {
		JsonObject jsonFormat = jsonRoot.getAsJsonObject("format");
		singleMessageFormat = jsonStringOr(jsonFormat, "singleMessage", "%u: %m");
		multiMessageFormat = jsonStringOr(jsonFormat, "multiMessage", "[%c] %u: %m");
		whisperMessageFormat = jsonStringOr(jsonFormat, "whisperMessage", "%u \u25b6 %r: %m");
		singleActionFormat = jsonStringOr(jsonFormat, "singleEmote", "%u %m");
		multiActionFormat = jsonStringOr(jsonFormat, "multiEmote", "[%c] %u %m");
		whisperActionFormat = jsonStringOr(jsonFormat, "whisperEmote", "%u \u25b6 %r : %m");
		useAnonymousLogin = jsonRoot.has("anonymousLogin") && jsonRoot.get("anonymousLogin").getAsBoolean();
		showWhispers = jsonRoot.has("showWhispers") && jsonRoot.get("showWhispers").getAsBoolean();
		JsonArray jsonChannels = jsonRoot.getAsJsonArray("channels");
		for(int i = 0; i < jsonChannels.size(); i++) {
			JsonObject jsonChannel = jsonChannels.get(i).getAsJsonObject();
			TwitchChannel channel = new TwitchChannel(jsonChannel.get("name").getAsString());
			channel.setSubscribersOnly(jsonChannel.has("subscribersOnly") && jsonChannel.get("subscribersOnly").getAsBoolean());
			channel.setDeletedMessages(TwitchChannel.DeletedMessages.fromName(jsonChannel.get("deletedMessages").getAsString()));
			channel.setTargetTabName(jsonStringOr(jsonChannel, "targetTab", channel.getName()));
			channel.setActive(jsonChannel.has("active") && jsonChannel.get("active").getAsBoolean());
			twitchManager.addChannel(channel);
		}
	}

	private static JsonObject save(JsonObject jsonRoot, TwitchManager twitchManager) {
		JsonObject jsonFormat = new JsonObject();
		jsonFormat.addProperty("singleMessage", singleMessageFormat);
		jsonFormat.addProperty("multiMessage", multiMessageFormat);
		jsonFormat.addProperty("whisperMessage", whisperMessageFormat);
		jsonFormat.addProperty("singleEmote", singleActionFormat);
		jsonFormat.addProperty("multiEmote", multiActionFormat);
		jsonFormat.addProperty("whisperEmote", whisperActionFormat);
		jsonRoot.add("format", jsonFormat);
		jsonRoot.addProperty("anonymousLogin", useAnonymousLogin);
		jsonRoot.addProperty("showWhispers", showWhispers);
		JsonArray jsonChannels = new JsonArray();
		for(TwitchChannel channel : twitchManager.getChannels()) {
			JsonObject jsonChannel = new JsonObject();
			jsonChannel.addProperty("name", channel.getName());
			jsonChannel.addProperty("subscribersOnly", channel.isSubscribersOnly());
			jsonChannel.addProperty("deletedMessages", channel.getDeletedMessages().name().toLowerCase());
			jsonChannel.addProperty("targetTab", channel.getTargetTabName());
			jsonChannel.addProperty("active", channel.isActive());
			jsonChannels.add(jsonChannel);
		}
		jsonRoot.add("channels", jsonChannels);
		return jsonRoot;
	}

	private static String jsonStringOr(JsonObject object, String key, String defaultVal) {
		return object.has(key) ? object.get(key).getAsString() : defaultVal;
	}
}
