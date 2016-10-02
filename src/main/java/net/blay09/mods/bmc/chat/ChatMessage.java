package net.blay09.mods.bmc.chat;

import net.blay09.mods.bmc.api.chat.IChatChannel;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.api.chat.IChatMessage;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.blay09.mods.bmc.image.ChatImageDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class ChatMessage implements IChatMessage {

    private final int id;
    private ITextComponent chatComponent;
    private int backgroundColor;
    private IChatImage[] images;
	private int[] rgbColors;
    private NBTTagCompound customData;
	private long timestamp;
	private IChatChannel exclusiveChannel;
	private boolean managed;

	public ChatMessage(int id, ITextComponent chatComponent) {
        this.id = id;
        this.chatComponent = chatComponent;
		this.timestamp = System.currentTimeMillis();
    }

    @Override
    public int getId() {
        return id;
    }

	@Override
	public ITextComponent getChatComponent() {
		return chatComponent;
	}

	@Override
	public void setChatComponent(ITextComponent chatComponent) {
		this.chatComponent = chatComponent;
	}

	@Override
    public NBTTagCompound getCustomData() {
        return customData;
    }

    @Override
    public boolean hasData() {
        return customData != null;
    }

    @Override
    public boolean hasBackgroundColor() {
        return backgroundColor != 0;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

	@Override
	public IChatMessage withImages(int count) {
		images = new IChatImage[count];
		return this;
	}

	@Override
	@Nullable
	public IChatImage[] getImages() {
		return images;
	}

	@Override
	@Nullable
	public IChatImage getImage(int index) {
		if(images == null || index < 0 || index >= images.length) {
			return null;
		}
		return images[index];
	}

	@Override
	public IChatMessage setImage(int index, IChatRenderable image, ITooltipProvider tooltip) {
		setImage(index, new ChatImageDefault(index, image, tooltip));
		return this;
	}

	@Override
    public IChatMessage setImage(int index, IChatImage image) {
		if(index >= 0 && index < images.length) {
			images[index] = image;
		}
		return this;
    }

	@Override
	public IChatMessage withRGB(int count) {
		rgbColors = new int[count];
		for(int i = 0; i < rgbColors.length; i++) {
			rgbColors[i] = 0xFFFFFF;
		}
		return this;
	}

	@Override
	public IChatMessage setRGBColor(int index, int color) {
		if(index >= 0 && index < rgbColors.length) {
			rgbColors[index] = color;
		}
		return this;
	}

	@Override
	public int getRGBColor(int index) {
		if(rgbColors == null || index < 0 || index >= rgbColors.length) {
			return 0xFFFFFF;
		}
		return rgbColors[index];
	}

	public boolean hasRGBColors() {
		return rgbColors != null;
	}

	@Override
    public boolean hasImages() {
        return images != null;
    }

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public void setManaged(boolean managed) {
		this.managed = managed;
	}

	@Override
	public boolean isManaged() {
		return managed;
	}

	@Override
	public void clearImages() {
		images = null;
	}

	public ChatMessage copy() {
		ChatMessage out = new ChatMessage(id, chatComponent);
		out.backgroundColor = backgroundColor;
		if(images != null) {
			out.images = images; // TODO bad copy
		}
		if(customData != null) {
			out.customData = customData.copy();
		}
		out.timestamp = timestamp;
		return out;
	}

	public void setExclusiveChannel(IChatChannel exclusiveChannel) {
		this.exclusiveChannel = exclusiveChannel;
	}

	public boolean isExclusiveChannel() {
		return exclusiveChannel != null;
	}

	public IChatChannel getExclusiveChannel() {
		return exclusiveChannel;
	}
}
