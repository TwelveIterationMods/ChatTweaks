package net.blay09.mods.chattweaks.imagepreview;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class DownloadingTextureWithMetadata extends DownloadingTexture {

    private static final Logger logger = LogManager.getLogger();

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

    @Override
    public void loadTexture(IResourceManager manager) throws IOException {
        if (this.future == null) {
            NativeImage nativeImage;
            if (this.cacheFile != null && this.cacheFile.isFile()) {
                logger.debug("Loading http texture from local cache ({})", this.cacheFile);
                FileInputStream fileInputStream = new FileInputStream(this.cacheFile);
                nativeImage = this.loadTexture(fileInputStream);
            } else {
                nativeImage = null;
            }

            if (nativeImage != null) {
                this.setImage(nativeImage);
            } else {
                this.future = CompletableFuture.runAsync(() -> {
                    HttpURLConnection connection = null;
                    logger.debug("Downloading http texture from {} to {}", this.imageUrl, this.cacheFile);

                    try {
                        connection = (HttpURLConnection)(new URL(this.imageUrl)).openConnection(Minecraft.getInstance().getProxy());
                        connection.setRequestProperty("User-Agent", "ChatTweaks/1.0.0");
                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.connect();
                        if (connection.getResponseCode() / 100 == 2) {
                            InputStream inputStream;
                            if (this.cacheFile != null) {
                                FileUtils.copyInputStreamToFile(connection.getInputStream(), this.cacheFile);
                                inputStream = new FileInputStream(this.cacheFile);
                            } else {
                                inputStream = connection.getInputStream();
                            }

                            Minecraft.getInstance().execute(() -> {
                                NativeImage loadedNativeImage = this.loadTexture(inputStream);
                                if (loadedNativeImage != null) {
                                    this.setImage(loadedNativeImage);
                                }

                            });
                        }
                    } catch (Exception e) {
                        logger.error("Couldn't download http texture", e);
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }

                    }

                }, Util.getServerExecutor());
            }
        }
    }
}
