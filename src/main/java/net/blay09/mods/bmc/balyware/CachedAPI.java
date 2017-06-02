package net.blay09.mods.bmc.balyware;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.Minecraft;

import javax.annotation.Nullable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CachedAPI {

	private static final Gson gson = new Gson();
	private static final long DEFAULT_CACHE_TIME = 1000*60*60*24;

	@Nullable
	public static JsonObject loadCachedAPI(String url, String fileName, @Nullable String accept) {
		return loadCachedAPI(url, fileName, DEFAULT_CACHE_TIME, accept);
	}

	@Nullable
	public static JsonObject loadCachedAPI(String url, String fileName, long maxCacheTime, @Nullable String accept) {
		return loadCachedAPI(url, new File(getCacheDirectory(), fileName), maxCacheTime, accept);
	}

	@Nullable
	public static JsonObject loadCachedAPI(String url, File cacheFile, long maxCacheTime, @Nullable String accept) {
		JsonObject result = loadLocal(cacheFile, false, maxCacheTime);
		if(result == null) {
			result = loadRemote(url, accept);
			if(result == null) {
				result = loadLocal(cacheFile, true, maxCacheTime);
			} else {
				try(FileWriter writer = new FileWriter(cacheFile)) {
					gson.toJson(result, writer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	@Nullable
	private static JsonObject loadLocal(File file, boolean force, long maxCacheTime) {
		if(file.exists() && (force || System.currentTimeMillis() - file.lastModified() < maxCacheTime)) {
			try(FileReader reader = new FileReader(file)) {
				return gson.fromJson(reader, JsonObject.class);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Nullable
	private static JsonObject loadRemote(String url, @Nullable String accept) {
		try {
			URL apiURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
			if(accept != null) {
				connection.setRequestProperty("Accept", accept);
			}
			try(InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
				try {
					return gson.fromJson(reader, JsonObject.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File getCacheDirectory() {
		File file = new File(Minecraft.getMinecraft().mcDataDir, "ChatTweaks/cache/");
		if(!file.exists() && !file.mkdirs()) {
			throw new RuntimeException("Could not create cache directory for Chat Tweaks.");
		}
		return file;
	}

}
