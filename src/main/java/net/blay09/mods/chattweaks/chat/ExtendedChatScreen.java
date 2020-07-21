package net.blay09.mods.chattweaks.chat;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.chattweaks.api.event.ChatComponentClickEvent;
import net.blay09.mods.chattweaks.api.event.ChatComponentHoverEvent;
import net.blay09.mods.chattweaks.api.event.ExtendedClientChatEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.util.text.Style;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;

public class ExtendedChatScreen extends ChatScreen  {

    public ExtendedChatScreen(ChatScreen originalScreen) {
        this(originalScreen.defaultInputFieldText);
    }

    public ExtendedChatScreen(String defaultText) {
        super(defaultText);
    }

    @Override
    public void sendMessage(String message, boolean addToSentMessages) {
        //noinspection ConstantConditions
        sendMessageExtended(minecraft, message, addToSentMessages);
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

    public static void sendMessageExtended(Minecraft minecraft, String message, boolean addToSentMessages) {
        ExtendedClientChatEvent event = new ExtendedClientChatEvent(message, addToSentMessages);
        message = MinecraftForge.EVENT_BUS.post(event) ? "" : event.getMessage();
        addToSentMessages = event.isAddToSentMessages();
        if (message.isEmpty()) {
            return;
        }

        if (addToSentMessages) {
            minecraft.ingameGUI.getChatGUI().addToSentMessages(event.getHistoryOverride() != null ? event.getHistoryOverride() : message);
        }

        //noinspection ConstantConditions
        minecraft.player.sendChatMessage(message);
    }

}

