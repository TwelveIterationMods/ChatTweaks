package net.blay09.mods.chattweaks.gui.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID)
public class SideChatRenderer {

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

    private static final List<SideChatMessage> messages = Lists.newArrayList();

    public static void addMessage(ChatMessage chatMessage) {
        for (SideChatMessage message : messages) {
            message.y -= Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2;
        }
        messages.add(new SideChatMessage(chatMessage, 0, MESSAGE_TIME));
        if (messages.size() > MAX_MESSAGES) {
            messages.remove(0);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void onDrawOverlayChat(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || messages.isEmpty()) {
            return;
        }

        final int height = 64;
        int guiTop = event.getWindow().getScaledHeight() - height;
        int guiLeft = event.getWindow().getScaledWidth();
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiLeft, guiTop, 0f);
        RenderSystem.scalef(SCALE, SCALE, 1f);
        RenderSystem.enableBlend();
        for (int i = messages.size() - 1; i >= 0; i--) {
            SideChatMessage message = messages.get(i);
            message.timeLeft -= event.getPartialTicks();
            int alpha = 255;
            if (message.timeLeft < MESSAGE_TIME / 5f) {
                alpha = (int) Math.max(11, (255f * (message.timeLeft / (MESSAGE_TIME / 5f))));
            }

            if (message.timeLeft <= 0) {
                messages.remove(i);
            }

            String formattedText = message.chatMessage.getTextComponent().getFormattedText();
            Minecraft.getInstance().fontRenderer.drawString(formattedText, -Minecraft.getInstance().fontRenderer.getStringWidth(formattedText) - 16, message.y, 0xFFFFFF + (alpha << 24), true);
        }
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }
}
