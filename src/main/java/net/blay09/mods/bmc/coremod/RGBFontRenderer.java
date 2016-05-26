package net.blay09.mods.bmc.coremod;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;
import java.util.List;

public class RGBFontRenderer {

	private static int index;
	private static List<Integer> buffer;
	private static boolean lastShadow;

	public static void popColor(FontRenderer fontRenderer, boolean shadow) {
		if(shadow != lastShadow) {
			index = -1;
			lastShadow = shadow;
		}
		index++;
		if(buffer == null || index < 0 || index >= buffer.size()) {
			return;
		}
		int color = buffer.get(index);
		if(shadow) {
			GlStateManager.color((color >> 16) / 255f / 4, (float) (color >> 8 & 255) / 255f / 4, (float) (color & 255) / 255f / 4, fontRenderer.alpha);
		} else {
			GlStateManager.color((color >> 16) / 255f, (float) (color >> 8 & 255) / 255f, (float) (color & 255) / 255f, fontRenderer.alpha);
		}
	}

	public static void setBuffer(List<Integer> buffer) {
		index = -1;
		RGBFontRenderer.buffer = buffer;
	}

}
