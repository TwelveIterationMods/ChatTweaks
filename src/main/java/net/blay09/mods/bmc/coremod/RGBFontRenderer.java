package net.blay09.mods.bmc.coremod;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Arrays;

public class RGBFontRenderer {

	private static int[] colors = new int[8];
	private static int[] shadows = new int[8];
	private static int index;

	public static void pushColor(int color, int shadow) {
		if(index >= colors.length) {
			if(colors.length >= 64) { // just in case something or someone messes up, let's not go overboard
				throw new RuntimeException("RGB Font Renderer max buffer size exceeded");
			}
			colors = Arrays.copyOf(colors, colors.length * 2);
			shadows = Arrays.copyOf(shadows, shadows.length * 2);
		}
		colors[index] = color;
		shadows[index]= shadow;
		index++;
	}

	public static void popColor(FontRenderer fontRenderer, boolean shadow) {
		index--;
		if(index < 0) {
			index = 0;
		}
		if(shadow) {
			GlStateManager.color((shadows[index] >> 16) / 255.0F, (float) (shadows[index] >> 8 & 255) / 255.0F, (float) (shadows[index] & 255) / 255.0F, fontRenderer.alpha);
		} else {
			GlStateManager.color((colors[index] >> 16) / 255.0F, (float) (colors[index] >> 8 & 255) / 255.0F, (float) (colors[index] & 255) / 255.0F, fontRenderer.alpha);
		}
	}

	public static void reset() {
		index = 0;
	}

}
