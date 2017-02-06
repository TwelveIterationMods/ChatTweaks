package net.blay09.mods.bmc;

import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.chat.emotes.IEmote;
import net.blay09.mods.bmc.chat.emotes.IEmoteGroup;
import net.blay09.mods.bmc.chat.emotes.IEmoteLoader;
import net.blay09.mods.bmc.image.renderable.ImageLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ChatTweaksAPI {

	private static final long IMAGE_CACHE_TIME = 1000 * 60 * 60 * 24 * 7;

	public static IEmoteGroup registerEmoteGroup(String name) {
		return EmoteRegistry.registerEmoteGroup(name);
	}

	public static IEmote registerEmote(String code, IEmoteLoader loader) {
		return EmoteRegistry.registerEmote(code, loader);
	}

	public static IEmote registerRegexEmote(String pattern, IEmoteLoader loader) {
		return EmoteRegistry.registerRegexEmote(pattern, loader);
	}

	public static void loadEmoteImage(IEmote emote, InputStream inputStream) throws IOException {
		if(!loadEmoteImageFromCache(emote)) {
			emote.setImage(ImageLoader.loadImage(inputStream, emote.getImageCacheFile()));
		}
	}

	public static void loadEmoteImage(IEmote emote, URI uri) throws IOException {
		if(!loadEmoteImageFromCache(emote)) {
			emote.setImage(ImageLoader.loadImage(uri, emote.getImageCacheFile()));
		}
	}

	public static void registerImageURLTransformer(SimpleImageURLTransformer urlTransformer) {
		ChatTweaks.registerImageURLTransformer(urlTransformer);
	}

	private static boolean loadEmoteImageFromCache(IEmote emote) {
		if(emote.getImageCacheFile() != null && emote.getImageCacheFile().exists() && emote.getImageCacheFile().lastModified() - System.currentTimeMillis() < IMAGE_CACHE_TIME) {
			try(FileInputStream in = new FileInputStream(emote.getImageCacheFile())) {
				emote.setImage(ImageLoader.loadImage(in, null));
				return true;
			} catch (IOException e) {
				System.err.println("Failed to load emote from cache: " + e.getMessage());
			}
		}
		return false;
	}
}
