package net.blay09.mods.chattweaks.api.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.Style;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ChatComponentHoverEvent extends Event {

    private final MatrixStack matrixStack;
    private final Style style;
    private final int x;
    private final int y;

    public ChatComponentHoverEvent(MatrixStack matrixStack, Style style, int x, int y) {
        this.matrixStack = matrixStack;
        this.style = style;
        this.x = x;
        this.y = y;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    public Style getStyle() {
        return style;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
