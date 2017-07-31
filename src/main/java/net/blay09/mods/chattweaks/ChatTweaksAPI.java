package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteGroup;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteLoader;
import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;
import net.blay09.mods.chattweaks.image.renderable.ImageLoader;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;

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

	@Nullable
	public static IChatRenderable loadImage(URI uri, File cacheFile) throws IOException {
		return ImageLoader.loadImage(uri, cacheFile);
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

	public static void registerImageURLTransformer(Function<String, String> urlTransformer) {
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
