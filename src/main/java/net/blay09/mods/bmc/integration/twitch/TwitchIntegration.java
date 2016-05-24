package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.javairc.IRCConfiguration;
import net.blay09.javatmi.TMIClient;
import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchAuthentication;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, clientSideOnly = true, dependencies = "required-after:betterminecraftchat")
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	private static TextureAtlasSprite icon;

	private static final Map<String, TwitchChannel> channels = Maps.newHashMap();
	private static final List<TwitchChannel> activeChannels = Lists.newArrayList();

	public static boolean useAnonymousLogin;
	public static boolean showWhispers;
	public static String singleMessageFormat;
	public static String multiMessageFormat; 
	public static String singleActionFormat;
	public static String multiActionFormat;
	public static String whisperMessageFormat;
	public static String whisperActionFormat;

	private static TMIClient twitchClient;

	@Nullable
	public static TMIClient getTwitchClient() {
		return twitchClient;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		ClientCommandHandler.instance.registerCommand(new CommandTwitch());

		Gson gson = new Gson();
		try (FileReader reader = new FileReader(new File(event.getModConfigurationDirectory(), "BetterMinecraftChat/twitchintegration.json"))) {
			JsonObject jsonRoot = gson.fromJson(reader, JsonObject.class);
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
				channel.setTargetChannelName(jsonStringOr(jsonChannel, "targetTab", channel.getName()));
				channel.setActive(jsonChannel.has("active") && jsonChannel.get("active").getAsBoolean());
				channels.put(channel.getName().toLowerCase(), channel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String jsonStringOr(JsonObject object, String key, String defaultVal) {
		return object.has(key) ? object.get(key).getAsString() : defaultVal;
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BetterMinecraftChatAPI.registerIntegration(this);

		TwitchBadge.loadInbuiltBadge("broadcaster");
		TwitchBadge.loadInbuiltBadge("moderator");
		TwitchBadge.loadInbuiltBadge("turbo");
		TwitchBadge.loadInbuiltBadge("staff");
		TwitchBadge.loadInbuiltBadge("admin");
		TwitchBadge.loadInbuiltBadge("global_mod");
	}

	@SubscribeEvent
	public void onWorldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		connect();
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		icon = event.getMap().registerSprite(new ResourceLocation(MOD_ID, "icon"));
	}

	@Override
	public String getModId() {
		return MOD_ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TextureAtlasSprite getIcon() {
		return icon;
	}

	@Override
	public GuiScreen getConfigScreen(GuiScreen parentScreen) {
		return new GuiTwitchAuthentication(parentScreen);
	}

	@Nullable
	public static TwitchChannel getTwitchChannel(String channel) {
		return channels.get(channel.charAt(0) == '#' ? channel.substring(1).toLowerCase() : channel.toLowerCase());
	}

	public static Collection<TwitchChannel> getTwitchChannels() {
		return channels.values();
	}

	public static void addTwitchChannel(TwitchChannel channel) {
		channels.put(channel.getName().toLowerCase(), channel);
		updateChannelStates();
	}

	public static void removeTwitchChannel(TwitchChannel channel) {
		channels.remove(channel.getName().toLowerCase());
		if(activeChannels.remove(channel)) {
			if(twitchClient != null) {
				twitchClient.part("#" + channel.getName().toLowerCase());
			}
		}
	}

	public static void updateChannelStates() {
		activeChannels.clear();
		for(TwitchChannel channel : channels.values()) {
			if(channel.isActive()) {
				activeChannels.add(channel);
				if(twitchClient != null) {
					twitchClient.join("#" + channel.getName().toLowerCase());
				}
			} else {
				if(twitchClient != null) {
					twitchClient.part("#" + channel.getName().toLowerCase());
				}
			}
		}
	}

	public static boolean isMultiMode() {
		return activeChannels.size() > 1;
	}

	public static void connect() {
		for(TwitchChannel channel : channels.values()) {
			channel.setTargetChannel(BetterMinecraftChatAPI.getChatChannel(channel.getTargetChannelName(), false));
		}

		AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
		if(tokenPair != null) {
			String token = tokenPair.getToken().startsWith("oauth:") ? tokenPair.getToken() : "oauth:" + tokenPair.getToken();
			IRCConfiguration.IRCConfigurationBuilder builder = TMIClient.defaultBuilder().debug(true).nick(tokenPair.getUsername()).password(token);
			for(TwitchChannel channel : channels.values()) {
				if(channel.isActive()) {
					builder.autoJoinChannel("#" + channel.getName().toLowerCase());
				}
			}
			twitchClient = new TMIClient(builder.build(), new TwitchChatHandler());
			twitchClient.connect();
		}
	}

	public static boolean isConnected() {
		return twitchClient != null && twitchClient.getIRCConnection().isConnected();
	}

	public static void disconnect() {
		if(twitchClient != null) {
			twitchClient.disconnect();
			twitchClient = null;
		}
	}
}
