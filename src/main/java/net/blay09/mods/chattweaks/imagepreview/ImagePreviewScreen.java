package net.blay09.mods.chattweaks.imagepreview;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

public class ImagePreviewScreen extends Screen {

    private static final float IMAGE_WIDTH = 300;
    private static final float IMAGE_HEIGHT = 150;

    private final URL url;
    private final URL directURL;

    private final Screen parentScreen;
    private final ResourceLocation textureResourceLocation;
    private final DownloadingTextureWithMetadata texture;
    private boolean textureLoaded;

    public ImagePreviewScreen(@Nullable Screen parentScreen, URL url, URL directURL) {
        super(new TranslationTextComponent("chattweaks.gui.imagePreview"));
        this.parentScreen = parentScreen;
        this.url = url;
        this.directURL = directURL;

        textureResourceLocation = new ResourceLocation(ChatTweaks.MOD_ID, "preview/" + UUID.randomUUID().toString().toLowerCase(Locale.ENGLISH));
        File cacheFile = new File(UUID.randomUUID().toString());
        texture = new DownloadingTextureWithMetadata(cacheFile, directURL.toString(), textureResourceLocation, false, () -> {
            textureLoaded = true;
            if (cacheFile.delete()) {
                cacheFile.deleteOnExit();
            }
        });
    }

    @Override
    public void init() {
        if (!textureLoaded) {
            //noinspection ConstantConditions
            minecraft.textureManager.loadTexture(textureResourceLocation, texture);
        }

        addButton(new Button(width / 2 - 150, height / 2 + 65, 90, 20, new TranslationTextComponent("chattweaks.gui.imagePreview.openInBrowser"), button -> {
            try {
                Util.getOSType().openURI(url.toURI());
            } catch (URISyntaxException e) {
                ChatTweaks.logger.error("An error occurred trying to open the link: ", e);
            }
            Minecraft.getInstance().displayGuiScreen(parentScreen);
        }));

        addButton(new Button(width / 2 - 50, height / 2 + 65, 100, 20, new TranslationTextComponent("chattweaks.gui.imagePreview.copyToClipboard"), button -> {
            Minecraft.getInstance().keyboardListener.setClipboardString(url.toString());
            Minecraft.getInstance().displayGuiScreen(parentScreen);
        }));

        addButton(new Button(width / 2 + 60, height / 2 + 65, 90, 20, new TranslationTextComponent("chattweaks.gui.imagePreview.close"), button -> Minecraft.getInstance().displayGuiScreen(parentScreen)));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        if (!textureLoaded) {
            drawCenteredString(matrixStack, font, I18n.format("chattweaks.gui.imagePreview.loadingPreview"), width / 2, height / 2 - 20, 0xFFFFFFFF);
        } else {
            float renderWidth = texture.getWidth();
            float renderHeight = texture.getHeight();
            float factor;
            if (renderWidth > IMAGE_WIDTH) {
                factor = IMAGE_WIDTH / renderWidth;
                renderWidth *= factor;
                renderHeight *= factor;
            }
            if (renderHeight > IMAGE_HEIGHT) {
                factor = IMAGE_HEIGHT / renderHeight;
                renderWidth *= factor;
                renderHeight *= factor;
            }
            int renderX = (int) (width / 2 - renderWidth / 2);
            int renderY = (int) (height / 2 - renderHeight / 2 - 20);
            texture.bindTexture();
            blit(matrixStack, renderX, renderY, (int) renderWidth, (int) renderHeight, 0, 0, texture.getWidth(), texture.getHeight(), texture.getWidth(), texture.getHeight());

            /*final int blitOffset = getBlitOffset();
            buffer.pos(renderX, renderY + renderHeight, blitOffset).tex(0, 1).endVertex();
            buffer.pos(renderX + renderWidth, renderY + renderHeight, blitOffset).tex(1, 1).endVertex();
            buffer.pos(renderX + renderWidth, renderY, blitOffset).tex(1, 0).endVertex();
            buffer.pos(renderX, renderY, blitOffset).tex(0, 0).endVertex();
            tessellator.draw();*/


            //blit(matrixStack, 0, 0, 0, texture.getWidth(), 0, 0);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
        super.onClose();

        texture.deleteGlTexture();
    }

}
