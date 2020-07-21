package net.blay09.mods.chattweaks.api;

import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface ChatMessage {
    int getChatLineId();

    ITextComponent getTextComponent();

    ITextComponent getSenderComponent();

    ITextComponent getMessageComponent();

    void setBackgroundColor(int color);

    int getBackgroundColor();

    long getTimestamp();

    void setExclusiveView(@Nullable ChatView view);

    @Nullable
    ChatView getExclusiveView();

    void setVariable(String key, @Nullable ITextComponent value);

    @Nullable
    ITextComponent getVariable(String key);
}
