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

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collection;

public interface InternalMethods {

	Collection<IEmote> getEmotes();
	IEmote registerEmote(String code, IEmoteLoader loader);
	IEmote registerRegexEmote(String regex, IEmoteLoader loader);
	IEmoteGroup registerEmoteGroup(String name);
	IChatRenderable loadImage(URI uri, File cacheFile) throws MalformedURLException;
	IChatRenderable loadImage(InputStream in, File cacheFile);
	void loadEmoteImage(IEmote emote, URI uri) throws MalformedURLException;
	void loadEmoteImage(IEmote emote, InputStream in);
	IChatImage createImage(int index, IChatRenderable image, ITooltipProvider tooltip);
	IChatMessage getChatLine(int id);
	IChatMessage addChatLine(ITextComponent chatComponent, NBTTagCompound data);
	void removeChatLine(int id);
	void registerImageURLTransformer(Function<String, String> function);
	IChatChannel getChatChannel(String name, boolean create);
	void clearChat();

}
