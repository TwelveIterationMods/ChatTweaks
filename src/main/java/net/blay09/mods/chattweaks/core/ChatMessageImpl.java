package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class ChatMessageImpl implements ChatMessage {

    private final int chatLineId;
    private final ITextComponent textComponent;
    private final ITextComponent senderComponent;
    private int backgroundColor;

    private ChatView exclusiveView;

    public ChatMessageImpl(int chatLineId, ITextComponent textComponent) {
        this.chatLineId = chatLineId;
        this.textComponent = textComponent;
        senderComponent = textComponent; // TODO
    }

    @Override
    public int getChatLineId() {
        return chatLineId;
    }

    @Override
    public ITextComponent getTextComponent() {
        return textComponent;
    }

    @Override
    public ITextComponent getSenderComponent() {
        return senderComponent;
    }

    @Override
    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setExclusiveView(@Nullable ChatView view) {
        exclusiveView = view;
    }

    @Override
    public ChatView getExclusiveView() {
        return exclusiveView;
    }
}
