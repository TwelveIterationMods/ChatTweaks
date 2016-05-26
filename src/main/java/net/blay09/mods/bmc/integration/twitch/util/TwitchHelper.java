package net.blay09.mods.bmc.integration.twitch.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.integration.twitch.TwitchIntegration;
import net.blay09.mods.bmc.integration.twitch.gui.GuiTwitchWaitingForUsername;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TwitchHelper {

	public static final String OAUTH_CLIENT_ID = "gdhi94otnk7c7746syjv7gkr6bizq4w";
	public static final String OAUTH_REDIRECT_URI = "http://localhost:" + TokenReceiver.PORT + "/token/";
	private static final String API_BASE_URL = "https://api.twitch.tv/kraken?api_version=3&client_id={{CLIENT_ID}}&oauth_token={{ACCESS_TOKEN}}";
	private static final String TWITCH_AUTHORIZE = "https://api.twitch.tv/kraken/oauth2/authorize?response_type=token&client_id={{CLIENT_ID}}&redirect_uri={{REDIRECT_URI}}&scope=chat_login&force_verify=true";

	private static TokenReceiver tokenReceiver;

	public static void listenForToken(final GuiScreen parentScreen, final Runnable callback) {
		if(tokenReceiver == null) {
			tokenReceiver = new TokenReceiver() {
				@Override
				public void onTokenReceived(final String token) {
					Minecraft.getMinecraft().addScheduledTask(new Runnable() {
						@Override
						public void run() {
							Minecraft.getMinecraft().displayGuiScreen(new GuiTwitchWaitingForUsername(parentScreen));
							requestUsername(token, callback);
						}
					});
				}
			};
			tokenReceiver.start();
		}
		try {
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			Object desktop = desktopClass.getMethod("getDesktop").invoke(null);
			desktopClass.getMethod("browse", URI.class).invoke(desktop, new URI(TWITCH_AUTHORIZE.replace("{{CLIENT_ID}}", OAUTH_CLIENT_ID).replace("{{REDIRECT_URI}}", OAUTH_REDIRECT_URI)));
		} catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void requestUsername(final String token, final Runnable callback) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					URL apiURL = new URL(API_BASE_URL.replace("{{CLIENT_ID}}", OAUTH_CLIENT_ID).replace("{{ACCESS_TOKEN}}", token));
					try (InputStreamReader reader = new InputStreamReader(apiURL.openStream())) {
						try {
							Gson gson = new Gson();
							JsonObject root = gson.fromJson(reader, JsonObject.class);
							String username = root.getAsJsonObject("token").get("user_name").getAsString();
							BetterMinecraftChatAPI.getAuthManager().storeToken(TwitchIntegration.MOD_ID, username, token);
							Minecraft.getMinecraft().addScheduledTask(callback);
						} catch (JsonParseException e) {
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

}
