package net.blay09.mods.bmc.handler;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class SideChatHandler {

	private static final int MAX_MESSAGES = 10;
	private static final float MESSAGE_TIME = 120;
	private static final float SCALE = 0.5f;

	private static class SideChatMessage {
		private final ChatMessage chatMessage;
		private int y;
		private float timeLeft;

		public SideChatMessage(ChatMessage chatMessage, int y, float timeLeft) {
			this.chatMessage = chatMessage;
			this.y = y;
			this.timeLeft = timeLeft;
		}
	}

	private final List<SideChatMessage> messages = Lists.newArrayList();

	public void addMessage(ChatMessage chatMessage) {
		for(SideChatMessage message : messages) {
			message.y -= Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2;
		}
		messages.add(new SideChatMessage(chatMessage, 0, MESSAGE_TIME));
		if(messages.size() > MAX_MESSAGES) {
			messages.remove(0);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unused")
	public void onDrawOverlayChat(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.ALL || messages.isEmpty()) {
			return;
		}
		final int height = 64;
		int guiTop = event.getResolution().getScaledHeight() - height;
		int guiLeft = event.getResolution().getScaledWidth();
		GlStateManager.pushMatrix();
		GlStateManager.translate(guiLeft, guiTop, 0f);
		GlStateManager.scale(SCALE, SCALE, 1f);
		GlStateManager.enableBlend();
		for(int i = messages.size() - 1; i >= 0; i--) {
			SideChatMessage message = messages.get(i);
			message.timeLeft -= event.getPartialTicks();
			int alpha = 255;
			if(message.timeLeft < MESSAGE_TIME / 5f) {
				alpha = (int) Math.max(11, (255f * (message.timeLeft / (MESSAGE_TIME / 5f))));
			}
			if(message.timeLeft <= 0) {
				messages.remove(i);
			}
			String formattedText = message.chatMessage.getChatComponent().getFormattedText();
			Minecraft.getMinecraft().fontRendererObj.drawString(formattedText, -Minecraft.getMinecraft().fontRendererObj.getStringWidth(formattedText) - 16, message.y, 0xFFFFFF + (alpha << 24), true);
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}
