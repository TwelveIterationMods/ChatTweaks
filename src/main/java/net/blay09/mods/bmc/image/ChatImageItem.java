package net.blay09.mods.bmc.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ChatImageItem extends ChatImage {

	private final ItemStack itemStack;

	public ChatImageItem(int index, ItemStack itemStack) {
		super(index);
		this.itemStack = itemStack;
	}

	@Override
	public void draw(int x, int y, int alpha) {
		renderItemModelIntoGUI(itemStack, x, y - 2, Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(itemStack, null, Minecraft.getMinecraft().thePlayer), alpha);
	}

	private void renderItemModelIntoGUI(ItemStack itemStack, int x, int y, IBakedModel model, int alpha) {
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
		GlStateManager.pushMatrix();
		textureManager.bindTexture(TextureMap.locationBlocksTexture);
		textureManager.getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		Minecraft.getMinecraft().getRenderItem().setupGuiTransform(x, y, model.isGui3d());
		model = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GUI, false);
		renderItem(itemStack, model, alpha);
		GlStateManager.disableAlpha();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		textureManager.bindTexture(TextureMap.locationBlocksTexture);
		textureManager.getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
	}

	private static void renderItem(ItemStack itemStack, IBakedModel model, int alpha) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(-0.5f, -0.5f, -0.5f);
		if (model.isBuiltInRenderer()) {
			GlStateManager.enableRescaleNormal();
			TileEntityItemStackRenderer.instance.renderByItem(itemStack);
		} else {
			if(model.isGui3d()) {
				GlStateManager.disableLighting();
			}
			Minecraft.getMinecraft().getRenderItem().renderModel(model, 0x00FFFFFF | (alpha << 24), itemStack);
			if (itemStack.hasEffect()) {
				Minecraft.getMinecraft().getRenderItem().renderEffect(model);
			}
		}
		GlStateManager.popMatrix();
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public float getScale() {
		return 0.7f;
	}
}
