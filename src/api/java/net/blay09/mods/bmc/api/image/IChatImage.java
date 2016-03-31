package net.blay09.mods.bmc.api.image;

import java.util.List;

public interface IChatImage {
	void draw(int x, int y, int alpha);

	int getIndex();

	int getSpaces();

	int getWidth();

	int getHeight();

	float getScale();

	List<String> getTooltip();
}
