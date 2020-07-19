package net.blay09.mods.chattweaks.chat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.blay09.mods.chattweaks.chatimage.ChatImage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class ChatMessage {

    private final int id;
    private ITextComponent chatComponent;
    private int backgroundColor;
    private List<ChatImage> images;
	private int[] rgbColors;
    private CompoundNBT customData;
	private long timestamp;
	private ChatView exclusiveView;
	private ITextComponent sender;
	private ITextComponent message;
	private Map<String, ITextComponent> outputVars;

	public ChatMessage(int id, ITextComponent chatComponent) {
        this.id = id;
        this.chatComponent = chatComponent;
		this.timestamp = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

	public ITextComponent getTextComponent() {
		return chatComponent;
	}

	public void setTextComponent(ITextComponent chatComponent) {
		this.chatComponent = chatComponent;
	}

    public CompoundNBT getCustomData() {
        return customData;
    }

    public boolean hasData() {
        return customData != null;
    }

    public boolean hasBackgroundColor() {
        return backgroundColor != 0;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

	@Nullable
	public List<ChatImage> getImages() {
		return images;
	}

	@Nullable
	public ChatImage getImage(int index) {
		if(images == null || index < 0 || index >= images.size()) {
			return null;
		}
		return images.get(index);
	}

    public ChatMessage addImage(ChatImage image) {
		if(images == null) {
			images = Lists.newArrayList(image);
		} else {
			images.add(image);
		}
		return this;
    }

	public ChatMessage withRGB(int count) {
		rgbColors = new int[count];
		for(int i = 0; i < rgbColors.length; i++) {
			rgbColors[i] = 0xFFFFFF;
		}
		return this;
	}

	public ChatMessage setRGBColor(int index, int color) {
		if(index >= 0 && index < rgbColors.length) {
			rgbColors[index] = color;
		}
		return this;
	}

	public int getRGBColor(int index) {
		if(rgbColors == null || index < 0 || index >= rgbColors.length) {
			return 0xFFFFFF;
		}
		return rgbColors[index];
	}

	public boolean hasRGBColors() {
		return rgbColors != null;
	}

    public boolean hasImages() {
        return images != null;
    }

	public long getTimestamp() {
		return timestamp;
	}

	public void clearImages() {
		images = null;
	}

	public ChatMessage copy() {
		ChatMessage out = new ChatMessage(id, chatComponent);
		out.backgroundColor = backgroundColor;
		if(images != null) {
			out.images = images;
		}
		out.rgbColors = rgbColors;
		out.outputVars = outputVars;
		if(customData != null) {
			out.customData = customData.copy();
		}
		out.timestamp = timestamp;
		out.sender = sender;
		out.message = message;
		return out;
	}

	public void setExclusiveView(ChatView view) {
		this.exclusiveView = view;
	}

	public boolean hasExclusiveView() {
		return exclusiveView != null;
	}

	public ChatView getExclusiveView() {
		return exclusiveView;
	}

	public void setSender(@Nullable ITextComponent sender) {
		this.sender = sender;
	}

	@Nullable
	public ITextComponent getSender() {
		return sender;
	}

	@Nullable
	public ITextComponent getMessage() {
		return message;
	}

	public void setMessage(ITextComponent message) {
		this.message = message;
	}

	public void setOutputVar(String key, @Nullable ITextComponent value) {
		if(outputVars == null) {
			outputVars = Maps.newHashMap();
		}
		outputVars.put(key, value);
	}

	@Nullable
	public ITextComponent getOutputVar(String key) {
		return outputVars != null ? outputVars.get(key) : null;
	}

	@Override
	public String toString() {
		return chatComponent.getString();
	}
}
