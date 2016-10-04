package net.blay09.mods.bmc;

import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.chat.emotes.IEmote;
import net.blay09.mods.bmc.chat.emotes.IEmoteGroup;
import net.blay09.mods.bmc.chat.emotes.IEmoteLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ChatTweaksAPI {
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

	}


	public static void loadEmoteImage(IEmote emote, URI uri) throws IOException {

	}

	public static void registerImageURLTransformer(SimpleImageURLTransformer urlTransformer) {

	}
}
