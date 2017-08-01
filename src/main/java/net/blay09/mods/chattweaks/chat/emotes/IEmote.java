package net.blay09.mods.chattweaks.chat.emotes;

import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public interface IEmote {

	Object getCustomData();

	void setCustomData(Object customData);

	List<String> getTooltip();

	void addTooltip(String... tooltip);

	String getCode();

	Pattern getPattern();

	IEmoteLoader getLoader();

	IChatRenderable getImage();

	void setImage(IChatRenderable image);

	int getWidthInSpaces();

	void requestLoad();

	void setImageCacheFile(String fileName);

	@Nullable
	File getImageCacheFile();

	boolean isRegex();
}
