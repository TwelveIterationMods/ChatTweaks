package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.blay09.javairc.IRCConfiguration;
import net.blay09.javatmi.TMIClient;
import net.blay09.mods.bmc.AuthManager;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.api.event.PrintChatMessageEvent;
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
import java.util.List;

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, dependencies = "required-after:betterminecraftchat")
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	private static TextureAtlasSprite icon;

	private static final List<TwitchChannel> channels = Lists.newArrayList();
	public static boolean useAnonymousLogin;
	public static boolean showWhispers;
	public static String singleMessageFormat;
	public static String multiMessageFormat; 
	public static String singleEmoteFormat; 
	public static String multiEmoteFormat;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		Gson gson = new Gson();
		try (FileReader reader = new FileReader(new File(event.getModConfigurationDirectory(), ""))) {
			JsonObject jsonRoot = gson.fromJson(reader, JsonObject.class);
			JsonObject jsonFormat = jsonRoot.getAsJsonObject("format");
			singleMessageFormat = jsonFormat.get("singleMessage").getAsString();
			multiMessageFormat = jsonFormat.get("multiMessage").getAsString();
			singleEmoteFormat = jsonFormat.get("singleEmote").getAsString();
			multiEmoteFormat = jsonFormat.get("multiEmote").getAsString();
			useAnonymousLogin = jsonRoot.has("anonymousLogin") && jsonRoot.get("anonymousLogin").getAsBoolean();
			showWhispers = jsonRoot.has("showWhispers") && jsonRoot.get("showWhispers").getAsBoolean();
			JsonArray jsonChannels = jsonRoot.getAsJsonArray("channels");
			for(int i = 0; i < jsonChannels.size(); i++) {
				JsonObject jsonChannel = jsonChannels.get(i).getAsJsonObject();
				TwitchChannel channel = new TwitchChannel(jsonChannel.get("name").getAsString());
				channel.setSubscribersOnly(jsonChannel.has("subscribersOnly") && jsonChannel.get("subscribersOnly").getAsBoolean());
				channel.setDeletedMessages(TwitchChannel.DeletedMessages.fromName(jsonChannel.get("deletedMessages").getAsString()));
				channels.add(channel);
				if(jsonChannel.has("active") && jsonChannel.get("active").getAsBoolean()) {
//					activeChannels.add(channel);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static void connect() {
		AuthManager.TokenPair tokenPair = AuthManager.getToken(TwitchIntegration.MOD_ID);
		if(tokenPair != null) {
			String token = tokenPair.getToken().startsWith("oauth:") ? tokenPair.getToken() : "oauth:" + tokenPair.getToken();
			IRCConfiguration config = TMIClient.defaultBuilder().debug(true).nick(tokenPair.getUsername()).password(token).autoJoinChannel("#playhearthstone").build();
			TMIClient connection = new TMIClient(config, new TwitchChatHandler());
			connection.connect();
		}
	}
}
