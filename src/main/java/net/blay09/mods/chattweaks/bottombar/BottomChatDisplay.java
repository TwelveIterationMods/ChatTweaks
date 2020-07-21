package net.blay09.mods.chattweaks.bottombar;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatDisplay;
import net.blay09.mods.chattweaks.api.ChatView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BottomChatDisplay implements ChatDisplay {

    private static final float MESSAGE_TIME = 80;
    private static final float SCALE = 0.8f;

    private ChatMessage chatMessage;
    private float timeLeft;

    public BottomChatDisplay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getName() {
        return "bottom";
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage, ChatView view) {
        if (!view.isMuted()) {
            this.chatMessage = chatMessage;
            timeLeft = MESSAGE_TIME;
        }
    }

    @SubscribeEvent
    public void onDrawOverlayChat(RenderGameOverlayEvent.Post event) {
        if (chatMessage == null || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        timeLeft -= event.getPartialTicks();
        int alpha = (int) (255f * (timeLeft / MESSAGE_TIME));
        if (timeLeft <= 0) {
            chatMessage = null;
            return;
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(event.getWindow().getScaledWidth() / 2f, event.getWindow().getScaledHeight() - 64, 0f);
        RenderSystem.scalef(SCALE, SCALE, 1f);
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        final ITextComponent textComponent = chatMessage.getTextComponent();
        int textWidth = fontRenderer.func_238414_a_(textComponent);
        int boxMarginX = 4;
        int boxMarginY = 3;
        int x = -textWidth / 2;
        int y = 0;
        int backgroundColor = 0x110111 + (alpha << 24);
        int borderColor2 = 0x28007F + (alpha << 24);
        AbstractGui.fill(event.getMatrixStack(), x - boxMarginX - 1, y - boxMarginY - 1, x + textWidth + boxMarginX + 1, y - boxMarginY, borderColor2);
        AbstractGui.fill(event.getMatrixStack(), x - boxMarginX - 1, y + fontRenderer.FONT_HEIGHT + boxMarginY, x + textWidth + boxMarginX + 1, y + fontRenderer.FONT_HEIGHT + boxMarginY + 1, borderColor2);
        AbstractGui.fill(event.getMatrixStack(), x - boxMarginX - 1, y - boxMarginY, x - boxMarginX, y + fontRenderer.FONT_HEIGHT + boxMarginY, borderColor2);
        AbstractGui.fill(event.getMatrixStack(), x + textWidth + boxMarginX, y - boxMarginY, x + textWidth + boxMarginX + 1, y + fontRenderer.FONT_HEIGHT + boxMarginY, borderColor2);
        AbstractGui.fill(event.getMatrixStack(), x - boxMarginX, y - boxMarginY, x + textWidth + boxMarginX, y + fontRenderer.FONT_HEIGHT + boxMarginY, backgroundColor);
        if (alpha > 12) {
            RenderSystem.enableBlend();
            fontRenderer.func_238422_b_(event.getMatrixStack(), textComponent, x, y, 0xFFFFFF + (alpha << 24));
        }

        RenderSystem.popMatrix();
    }
}
