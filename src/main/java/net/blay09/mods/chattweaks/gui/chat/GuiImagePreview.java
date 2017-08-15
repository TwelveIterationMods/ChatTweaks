package net.blay09.mods.chattweaks.gui.chat;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.balyware.BlayCommon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class GuiImagePreview extends GuiScreen {

	private static final int IMAGE_WIDTH = 300;
	private static final int IMAGE_HEIGHT = 150;

	private final URL url;
	private final URL directURL;

	private final GuiScreen parentScreen;
	private int textureId = -1;
	private BufferedImage loadBuffer;
	private float textureWidth;
	private float textureHeight;

	public GuiImagePreview(@Nullable GuiScreen parentScreen, URL url, URL directURL) {
		this.parentScreen = parentScreen;
		this.url = url;
		this.directURL = directURL;
	}

	@Override
	public void initGui() {
		if (textureId == -1) {
			new Thread(() -> {
				try {
					loadBuffer = ImageIO.read(directURL);
				} catch (IOException e) {
					ChatTweaks.logger.error("An error occurred trying to load the image preview: ", e);
				}
			}).start();
		}

		buttonList.add(new GuiButton(0, width / 2 - 150, height / 2 + 65, 90, 20, I18n.format(ChatTweaks.MOD_ID + ":gui.imagePreview.openInBrowser")));
		buttonList.add(new GuiButton(1, width / 2 - 50, height / 2 + 65, 100, 20, I18n.format(ChatTweaks.MOD_ID + ":gui.imagePreview.copyToClipboard")));
		buttonList.add(new GuiButton(2, width / 2 + 60, height / 2 + 65, 90, 20, I18n.format(ChatTweaks.MOD_ID + ":gui.imagePreview.close")));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		switch(button.id) {
			case 0:
				try {
					BlayCommon.openWebLink(url.toURI());
				} catch (URISyntaxException e) {
					ChatTweaks.logger.error("An error occurred trying to open the link: ", e);
				}
				Minecraft.getMinecraft().displayGuiScreen(parentScreen);
				break;
			case 1:
				setClipboardString(url.toString());
				Minecraft.getMinecraft().displayGuiScreen(parentScreen);
				break;
			case 2:
				Minecraft.getMinecraft().displayGuiScreen(parentScreen);
				break;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawWorldBackground(0);

		if (textureId == -1 && loadBuffer != null) {
			textureWidth = loadBuffer.getWidth();
			textureHeight = loadBuffer.getHeight();
			textureId = TextureUtil.glGenTextures();
			TextureUtil.uploadTextureImage(textureId, loadBuffer);
			loadBuffer = null;
		}
		if(textureId == -1) {
			drawCenteredString(fontRenderer, I18n.format(ChatTweaks.MOD_ID + ":gui.imagePreview.loadingPreview"), width / 2, height / 2 - 20, 0xFFFFFFFF);
		} else {
			float renderWidth = textureWidth;
			float renderHeight = textureHeight;
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
			float renderX = width / 2 - renderWidth / 2;
			float renderY = height / 2 - renderHeight / 2 - 20;
			GlStateManager.bindTexture(textureId);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(renderX, renderY + renderHeight, zLevel).tex(0, 1).endVertex();
			buffer.pos(renderX + renderWidth, renderY + renderHeight, zLevel).tex(1, 1).endVertex();
			buffer.pos(renderX + renderWidth, renderY, zLevel).tex(1, 0).endVertex();
			buffer.pos(renderX, renderY, zLevel).tex(0, 0).endVertex();
			tessellator.draw();
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		if (textureId != -1) {
			TextureUtil.deleteTexture(textureId);
			textureId = -1;
		}
	}

}