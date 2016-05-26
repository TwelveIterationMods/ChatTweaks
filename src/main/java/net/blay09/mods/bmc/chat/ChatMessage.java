package net.blay09.mods.bmc.chat;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.api.chat.IChatMessage;
import net.blay09.mods.bmc.api.image.IChatRenderable;
import net.blay09.mods.bmc.api.image.ITooltipProvider;
import net.blay09.mods.bmc.image.ChatImageDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class ChatMessage implements IChatMessage {

    private final int id;
    private ITextComponent chatComponent;
    private int backgroundColor;
    private List<IChatImage> images;
	private List<Integer> rgbBuffer;
    private NBTTagCompound customData;
	private long timestamp;
	private ChatChannel exclusiveChannel;
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
	public void addImage(int index, IChatRenderable image, ITooltipProvider tooltip) {
		addImage(new ChatImageDefault(index, image, tooltip));
	}

	@Override
    public void addImage(IChatImage image) {
        if(images == null) {
            images = Lists.newArrayList();
        }
        images.add(image);
    }

	@Override
	public void addRGBColor(int red, int green, int blue) {
		addRGBColor((red & 255) << 16 | (green & 255) << 8 | blue & 255);
	}

	private void addRGBColor(int color) {
		if(rgbBuffer == null) {
			rgbBuffer = Lists.newArrayList();
		}
		rgbBuffer.add(color);
	}

	public List<Integer> getRGBBuffer() {
		return rgbBuffer;
	}

	public boolean hasRGBColors() {
		return rgbBuffer != null;
	}

	@Override
    public boolean hasImages() {
        return images != null && !images.isEmpty();
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

	public List<IChatImage> getImages() {
        return images;
    }

	public ChatMessage copy() {
		ChatMessage out = new ChatMessage(id, chatComponent);
		out.backgroundColor = backgroundColor;
		if(images != null) {
			out.images = Lists.newArrayList(images);
		}
		if(customData != null) {
			out.customData = (NBTTagCompound) customData.copy();
		}
		out.timestamp = timestamp;
		return out;
	}

	public void setExclusiveChannel(ChatChannel exclusiveChannel) {
		this.exclusiveChannel = exclusiveChannel;
	}

	public boolean isExclusiveChannel() {
		return exclusiveChannel != null;
	}

	public ChatChannel getExclusiveChannel() {
		return exclusiveChannel;
	}
}
