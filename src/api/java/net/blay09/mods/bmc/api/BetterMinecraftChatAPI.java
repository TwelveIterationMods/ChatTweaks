package net.blay09.mods.bmc.api;

import com.google.common.base.Function;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.EnumHelper;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;

public class BetterMinecraftChatAPI {

	public static final TextFormatting TEXT_FORMATTING_RGB = EnumHelper.addEnum(TextFormatting.class, "RGB", new Class[] { String.class, char.class, boolean.class }, "RGB", '#', true);

	private static InternalMethods internalMethods;
	public static void _internal_setupAPI(InternalMethods internalMethods) {
		BetterMinecraftChatAPI.internalMethods = internalMethods;
	}

	public static Collection<IEmote> getEmotes() {
		return internalMethods.getEmotes();
	}

	public static IEmote registerEmote(String code, IEmoteLoader loader) {
		return internalMethods.registerEmote(code, loader);
	}

	public static IEmote registerRegexEmote(String regex, IEmoteLoader loader) {
		return internalMethods.registerRegexEmote(regex, loader);
	}

	public static IEmoteGroup registerEmoteGroup(String name) {
		return internalMethods.registerEmoteGroup(name);
	}

	public static IChatRenderable loadImage(URI uri, File cacheFile) throws MalformedURLException {
		return internalMethods.loadImage(uri, cacheFile);
	}

	public static IChatRenderable loadImage(InputStream in, File cacheFile) {
		return internalMethods.loadImage(in, cacheFile);
	}

	public static void loadEmoteImage(IEmote emote, URI uri) throws MalformedURLException {
		internalMethods.loadEmoteImage(emote, uri);
	}

	public static void loadEmoteImage(IEmote emote, InputStream in) {
		internalMethods.loadEmoteImage(emote, in);
	}

	public static IChatMessage getChatLine(int id) {
		return internalMethods.getChatLine(id);
	}

	public static IChatMessage addChatLine(ITextComponent component, NBTTagCompound data) {
		return internalMethods.addChatLine(component, data);
	}

	public static void removeChatLine(int id) {
		internalMethods.removeChatLine(id);
	}

	public static IChatImage createImage(int index, IChatRenderable image, ITooltipProvider tooltip) {
		return internalMethods.createImage(index, image, tooltip);
	}

	public static void registerImageURLTransformer(Function<String, String> function) {
		internalMethods.registerImageURLTransformer(function);
	}

	public static IChatChannel getChatChannel(String name, boolean create) {
		return internalMethods.getChatChannel(name, create);
	}

	public static void clearChat() {
		internalMethods.clearChat();
	}
}
