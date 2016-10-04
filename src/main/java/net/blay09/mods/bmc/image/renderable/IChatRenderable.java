package net.blay09.mods.bmc.image.renderable;

public interface IChatRenderable {

	int getWidthInSpaces();
	int getTextureId();
	void disposeTexture();
	int getWidth();
	int getHeight();
	float getScale();
	void setScale(float scale);
	int getTexCoordX();
	int getTexCoordY();
	int getSheetWidth();
	int getSheetHeight();

}
