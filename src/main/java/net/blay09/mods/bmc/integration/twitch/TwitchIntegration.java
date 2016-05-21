package net.blay09.mods.bmc.integration.twitch;

import com.google.common.collect.Lists;
import net.blay09.javatmi.TMIAdapter;
import net.blay09.javatmi.TMIClient;
import net.blay09.javatmi.TwitchUser;
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

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, dependencies = "required-after:betterminecraftchat")
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	private static TextureAtlasSprite icon;

	public static boolean useAnonymousLogin;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BetterMinecraftChatAPI.registerIntegration(this);
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
			TMIClient connection = new TMIClient(tokenPair.getUsername(), tokenPair.getToken(), Lists.newArrayList("blay09"), new TMIAdapter() {
				@Override
				public void onChatMessage(TMIClient client, String channel, TwitchUser user, String message) {
					System.out.println(message);
				}
			});
			connection.connect();
		}
	}
}
