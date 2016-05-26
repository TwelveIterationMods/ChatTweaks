package net.blay09.mods.bmc.integration.twitch;

import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.IntegrationModule;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchAuthentication;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchBadge;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchChatHandler;
import net.blay09.mods.bmc.integration.twitch.handler.TwitchManager;
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

import java.io.File;

@Mod(modid = TwitchIntegration.MOD_ID, name = TwitchIntegration.NAME, clientSideOnly = true, dependencies = "required-after:betterminecraftchat")
public class TwitchIntegration implements IntegrationModule {

	public static final String MOD_ID = "twitchintegration";
	public static final String NAME = "Twitch Integration";

	@Mod.Instance(MOD_ID)
	public static TwitchIntegration instance;

	private TextureAtlasSprite icon;

	private final TwitchManager twitchManager = new TwitchManager();
	private final TwitchChatHandler twitchChatHandler = new TwitchChatHandler(twitchManager);

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);

		ClientCommandHandler.instance.registerCommand(new CommandTwitch());

		TwitchIntegrationConfig.load(new File(event.getModConfigurationDirectory(), "BetterMinecraftChat/twitchintegration.json"));
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
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		icon = event.getMap().registerSprite(new ResourceLocation(MOD_ID, "icon"));
	}

	@SubscribeEvent
	public void onWorldJoined(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		twitchManager.connect();
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

	public static TwitchChatHandler getTwitchChatHandler() {
		return instance.twitchChatHandler;
	}

	public static TwitchManager getTwitchManager() {
		return instance.twitchManager;
	}
}
