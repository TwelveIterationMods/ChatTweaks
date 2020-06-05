package net.blay09.mods.chattweaks.chat.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.image.renderable.IChatRenderable;
import net.blay09.mods.chattweaks.image.renderable.NullRenderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;

public class Emote<T> implements IEmote<T> {

    private final String code;
    private final IEmoteSource<T> source;
    private final T customData;

    private IChatRenderable image = NullRenderable.INSTANCE;
    private boolean loadRequested;

    public Emote(String code, IEmoteSource<T> source, T customData) {
        this.code = code;
        this.source = source;
        this.customData = customData;
    }

    @Override
    public T getCustomData() {
        return customData;
    }

    @Override
    public List<String> getTooltip() {
        String mainTooltip = TextFormatting.YELLOW + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmote") + " " + TextFormatting.WHITE + code;
        return Lists.newArrayList(mainTooltip, source.getTooltip(customData));
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public IEmoteSource<T> getSource() {
        return source;
    }

    @Override
    public IChatRenderable getImage() {
        return image;
    }

    @Override
    public void setImage(@Nullable IChatRenderable image) {
        if (image == null) {
            image = NullRenderable.INSTANCE;
        }

        this.image = image;
        loadRequested = false;
    }

    @Override
    public int getWidthInSpaces() {
        return image != null ? image.getWidthInSpaces() : 4;
    }

    @Override
    public void requestLoad() {
        if (!loadRequested) {
            loadRequested = true;
            AsyncEmoteLoader.getInstance().loadAsync(this);
        }
    }

    @Override
    public File getImageCacheFile() {
        return new File(Minecraft.getMinecraft().mcDataDir, "ChatTweaks/cache/" + source.getCacheFileName(customData));
    }
}
