package net.blay09.mods.bmc.api.chat;

import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

public interface IChatMessage {

	int getId();

	ITextComponent getChatComponent();

	void setChatComponent(ITextComponent component);

	NBTTagCompound getCustomData();

	boolean hasData();

	boolean hasBackgroundColor();

	int getBackgroundColor();

	void setBackgroundColor(int backgroundColor);

	void addImage(int index, IChatRenderable image, ITooltipProvider tooltip);

	void addImage(IChatImage image);

	void addRGBColor(int red, int green, int blue);

	boolean hasImages();

	void clearImages();

	long getTimestamp();

	void setManaged(boolean managed);

	boolean isManaged();
}
