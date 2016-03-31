package net.blay09.mods.bmc;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.blay09.mods.bmc.api.IChatChannel;
import net.blay09.mods.bmc.api.IChatMessage;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.blay09.mods.bmc.api.InternalMethods;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.image.ChatImageDefault;
import net.blay09.mods.bmc.image.renderable.ImageLoader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;

public class InternalMethodsImpl implements InternalMethods {

	private static final long IMAGE_CACHE_TIME = 1000 * 60 * 60 * 24 * 7;

	@Override
	public Collection<IEmote> getEmotes() {
		ImmutableList.Builder<IEmote> builder = ImmutableList.builder();
		builder.addAll(EmoteRegistry.getEmotes());
		builder.addAll(EmoteRegistry.getRegexEmotes());
		return builder.build();
	}

	@Override
	public IEmote registerEmote(String code, IEmoteLoader loader) {
		return EmoteRegistry.registerEmote(code, loader);
	}

	@Override
	public IEmote registerRegexEmote(String regex, IEmoteLoader loader) {
		return EmoteRegistry.registerRegexEmote(regex, loader);
	}

	@Override
	public IChatRenderable loadImage(URI uri, File cacheFile) throws MalformedURLException {
		return ImageLoader.loadImage(uri, cacheFile);
	}

	@Override
	public IChatRenderable loadImage(InputStream in, File cacheFile) {
		return ImageLoader.loadImage(in, cacheFile);
	}

	@Override
	public IChatMessage getChatLine(int id) {
		return BetterMinecraftChat.getChatHandler().getChatLine(id);
	}

	@Override
	public IChatMessage addChatLine(ITextComponent chatComponent, NBTTagCompound data) {
		return BetterMinecraftChat.getChatHandler().addChatLine(chatComponent, data, true);
	}

	@Override
	public void removeChatLine(int id) {
		BetterMinecraftChat.getChatHandler().removeChatLine(id);
	}

	@Override
	public IChatImage createImage(int index, IChatRenderable image, ITooltipProvider tooltip) {
		return new ChatImageDefault(index, image, tooltip);
	}

	@Override
	public void registerImageURLTransformer(Function<String, String> function) {
		BetterMinecraftChat.registerImageURLTransformer(function);
	}

	@Override
	public IChatChannel getChatChannel(String name, boolean create) {
		IChatChannel channel = BetterMinecraftChat.getChatHandler().getChannel(name);
		if(channel == null && create) {
			channel = new ChatChannel(name);
		}
		return channel;
	}

	@Override
	public void clearChat() {
		BetterMinecraftChat.getChatHandler().clearChat();
	}

	@Override
	public IEmoteGroup registerEmoteGroup(String name) {
		return EmoteRegistry.registerEmoteGroup(name);
	}

	@Override
	public void loadEmoteImage(IEmote emote, URI uri) throws MalformedURLException {
		if(!loadEmoteImageFromCache(emote)) {
			emote.setImage(ImageLoader.loadImage(uri, emote.getImageCacheFile()));
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote, InputStream in) {
		if(!loadEmoteImageFromCache(emote)) {
			emote.setImage(ImageLoader.loadImage(in, emote.getImageCacheFile()));
		}
	}

	private boolean loadEmoteImageFromCache(IEmote emote) {
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
