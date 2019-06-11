package net.blay09.mods.chattweaks.chat.emotes;

import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public interface IEmote<T> {

	T getCustomData();

	List<String> getTooltip();

	String getCode();

	IEmoteSource<T> getSource();

	IChatRenderable getImage();

	void setImage(IChatRenderable image);

	int getWidthInSpaces();

	void requestLoad();

	@Nullable
	File getImageCacheFile();
}
