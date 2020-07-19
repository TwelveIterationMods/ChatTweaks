package net.blay09.mods.chattweaks.imagepreview;

import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.io.File;

public class DownloadingTextureWithMetadata extends DownloadingTexture {

    private int width;
    private int height;

    public DownloadingTextureWithMetadata(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, boolean legacySkinIn, @Nullable Runnable processTaskIn) {
        super(cacheFileIn, imageUrlIn, textureResourceLocation, legacySkinIn, processTaskIn);
    }

    @Override
    public void setImage(NativeImage nativeImage) {
        width = nativeImage.getWidth();
        height = nativeImage.getHeight();

        super.setImage(nativeImage);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
