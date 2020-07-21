package net.blay09.mods.chattweaks.api;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface ChatMessage {
    int getChatLineId();
    ITextComponent getTextComponent();
    ITextComponent getSenderComponent();
    void setBackgroundColor(int color);
    int getBackgroundColor();

    void setExclusiveView(@Nullable ChatView view);

    @Nullable
    ChatView getExclusiveView();
}
