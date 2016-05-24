package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.javairc.IRCConfiguration;
import net.blay09.javatmi.TMIClient;
import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.api.event.PrintChatMessageEvent;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchChannels;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchConnect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, dependencies = "required-after:betterminecraftchat")
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	private static TextureAtlasSprite icon;

	private static final Map<String, TwitchChannel> channels = Maps.newHashMap();
	public static boolean useAnonymousLogin;
	public static boolean showWhispers;
	public static String singleMessageFormat;
	public static String multiMessageFormat; 
	public static String singleEmoteFormat; 
	public static String multiEmoteFormat;
	public static String whisperMessageFormat;
	public static String whisperEmoteFormat;

	public static Collection<TwitchChannel> getTwitchChannels() {
		return channels.values();
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		Gson gson = new Gson();
		try (FileReader reader = new FileReader(new File(event.getModConfigurationDirectory(), "BetterMinecraftChat/twitchintegration.json"))) {
			JsonObject jsonRoot = gson.fromJson(reader, JsonObject.class);
			JsonObject jsonFormat = jsonRoot.getAsJsonObject("format");
			singleMessageFormat = jsonStringOr(jsonFormat, "singleMessage", "%u: %m");
			multiMessageFormat = jsonStringOr(jsonFormat, "multiMessage", "[%c] %u: %m");
			whisperMessageFormat = jsonStringOr(jsonFormat, "whisperMessage", "%u \u25b6 %r: %m");
			singleEmoteFormat = jsonStringOr(jsonFormat, "singleEmote", "%u %m");
			multiEmoteFormat = jsonStringOr(jsonFormat, "multiEmote", "[%c] %u %m");
			whisperEmoteFormat = jsonStringOr(jsonFormat, "whisperEmote", "%u \u25b6 %r : %m");
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
	public void onWhatever(PrintChatMessageEvent event) {
		if(event.getMessage().getFormattedText().contains("wuppa")) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitchConnect(Minecraft.getMinecraft().currentScreen));
		} else if(event.getMessage().getFormattedText().contains("moep")) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiTwitchChannels());
		}
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
		return new GuiTwitchConnect(parentScreen);
	}

	public static TwitchChannel getTwitchChannel(String channel) {
		return channels.get(channel.charAt(0) == '#' ? channel.substring(1).toLowerCase() : channel.toLowerCase());
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
			IRCConfiguration config = builder.build();
			TMIClient connection = new TMIClient(config, new TwitchChatHandler());
			connection.connect();
		}
	}
}
