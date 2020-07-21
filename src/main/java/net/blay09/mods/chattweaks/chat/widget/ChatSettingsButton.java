package net.blay09.mods.chattweaks.chat.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class ChatSettingsButton extends Button {
    public ChatSettingsButton(int x, int y) {
        super(x, y, 14, 12, new StringTextComponent("..."), button -> {
            //noinspection ConstantConditions
            Minecraft.getInstance().player.sendStatusMessage(new StringTextComponent("nope"), false);
        });
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (visible) {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            int textColor = 0xE0E0E0;
            if (packedFGColor != 0) {
                textColor = packedFGColor;
            } else if (!active) {
                textColor = 0xA0A0A0;
            } else if (isHovered) {
                textColor = 0xFFFFA0;
            }
            final int backgroundColor = Minecraft.getInstance().gameSettings.getChatBackgroundColor(Integer.MIN_VALUE);
            final int hoverBackgroundColor = 0xAA000000;
            fill(matrixStack, x, y, x + width, y + height, isHovered ? hoverBackgroundColor : backgroundColor);
            drawCenteredString(matrixStack, fontRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, textColor);
        }
    }
}
