package net.blay09.mods.chattweaks.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID)
public class BottomChatRenderer {

	private static final float MESSAGE_TIME = 80;
	private static final float SCALE = 0.8f;

	private static ChatMessage chatMessage;
	private static float timeLeft;

	public static void setMessage(ChatMessage chatMessage) {
		BottomChatRenderer.chatMessage = chatMessage;
		timeLeft = MESSAGE_TIME;
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public static void onDrawOverlayChat(RenderGameOverlayEvent.Post event) {
		if(chatMessage == null || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
			return;
		}
		timeLeft -= event.getPartialTicks();
		int alpha = (int) (255f * (timeLeft / MESSAGE_TIME));
		if(timeLeft <= 0) {
			chatMessage = null;
			return;
		}
		RenderSystem.pushMatrix();
		RenderSystem.translatef(event.getWindow().getScaledWidth() / 2f, event.getWindow().getScaledHeight() - 64, 0f);
		RenderSystem.scalef(SCALE, SCALE, 1f);
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		String formattedText = chatMessage.getTextComponent().toString().getFormattedText();
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
			RenderSystem.enableBlend();
			fontRenderer.drawString(formattedText, x, y, 0xFFFFFF + (alpha << 24), true);
		}

		RenderSystem.popMatrix();
	}
}
