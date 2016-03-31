package net.blay09.mods.bmc.api.emote;

import net.blay09.mods.bmc.api.image.IChatRenderable;

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

	File getImageCacheFile();

	boolean isRegex();
}
