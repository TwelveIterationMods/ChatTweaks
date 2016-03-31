package net.blay09.mods.bmc.handler;

import net.blay09.mods.bmc.chat.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BottomChatHandler {

	private static final float MESSAGE_TIME = 80;
	private static final float SCALE = 0.8f;

	private ChatMessage chatMessage;
	private float timeLeft;

	public void setMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
		this.timeLeft = MESSAGE_TIME;
	}

	@SubscribeEvent
	public void onDrawOverlayChat(RenderGameOverlayEvent.Post event) {
		if(chatMessage == null || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}
		timeLeft -= event.getPartialTicks();
		int alpha = (int) (255f * (timeLeft / MESSAGE_TIME));
		if(timeLeft <= 0) {
			chatMessage = null;
			return;
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(event.getResolution().getScaledWidth() / 2, event.getResolution().getScaledHeight() - 64, 0f);
		GlStateManager.scale(SCALE, SCALE, 1f);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
		String formattedText = chatMessage.getChatComponent().getFormattedText();
		int textWidth = fontRenderer.getStringWidth(formattedText);
		int boxMarginX = 4;
		int boxMarginY = 3;
		int x = -textWidth / 2;
		int y = 0;
		int backgroundColor = 0x110111 + (alpha << 24);
		int borderColor2 = 0x28007F + (alpha << 24);
		Gui.drawRect(x - boxMarginX - 1, y - boxMarginY - 1, x + textWidth + boxMarginX + 1, y - boxMarginY, borderColor2);
		Gui.drawRect(x - boxMarginX - 1, y + fontRenderer.FONT_HEIGHT + boxMarginY, x + textWidth + boxMarginX + 1, y + fontRenderer.FONT_HEIGHT + boxMarginY + 1, borderColor2);
		Gui.drawRect(x - boxMarginX - 1, y - boxMarginY, x - boxMarginX, y + fontRenderer.FONT_HEIGHT + boxMarginY, borderColor2);
		Gui.drawRect(x + textWidth + boxMarginX, y - boxMarginY, x + textWidth + boxMarginX + 1, y + fontRenderer.FONT_HEIGHT + boxMarginY, borderColor2);
		Gui.drawRect(x - boxMarginX, y - boxMarginY, x + textWidth + boxMarginX, y + fontRenderer.FONT_HEIGHT + boxMarginY, backgroundColor);
		if(alpha > 12) {
			GlStateManager.enableBlend();
			fontRenderer.drawString(formattedText, x, y, 0xFFFFFF + (alpha << 24), true);
		}

		GlStateManager.popMatrix();
	}
}
