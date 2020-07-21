package net.blay09.mods.chattweaks.chat;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.chattweaks.api.event.ChatComponentClickEvent;
import net.blay09.mods.chattweaks.api.event.ChatComponentHoverEvent;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ExtendedSleepScreen extends SleepInMultiplayerScreen {

    @Override
    public void sendMessage(String message, boolean addToSentMessages) {
        //noinspection ConstantConditions
        ExtendedChatScreen.sendMessageExtended(minecraft, message, addToSentMessages);
    }

    @Override
    protected void renderComponentHoverEffect(MatrixStack matrixStack, @Nullable Style style, int x, int y) {
        if (style == null || !MinecraftForge.EVENT_BUS.post(new ChatComponentHoverEvent(matrixStack, style, x, y))) {
            super.renderComponentHoverEffect(matrixStack, style, x, y);
        }
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        if (style != null && MinecraftForge.EVENT_BUS.post(new ChatComponentClickEvent(style))) {
            return true;
        }

        return super.handleComponentClicked(style);
    }
}
