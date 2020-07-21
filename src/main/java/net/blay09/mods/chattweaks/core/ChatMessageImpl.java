package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ChatMessageImpl implements ChatMessage {

    private final int chatLineId;
    private final ITextComponent textComponent;
    private final long timestamp;

    private ITextComponent senderComponent;
    private ITextComponent messageComponent;
    private int backgroundColor;
    private ChatView exclusiveView;

    private Map<String, ITextComponent> outputVars;

    public ChatMessageImpl(int chatLineId, ITextComponent textComponent) {
        this.chatLineId = chatLineId;
        this.textComponent = textComponent;
        this.timestamp = System.currentTimeMillis();
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
    public ITextComponent getMessageComponent() {
        return messageComponent;
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
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setExclusiveView(@Nullable ChatView view) {
        exclusiveView = view;
    }

    @Override
    public ChatView getExclusiveView() {
        return exclusiveView;
    }

    public void setSenderComponent(ITextComponent senderComponent) {
        this.senderComponent = senderComponent;
    }

    public void setMessageComponent(ITextComponent messageComponent) {
        this.messageComponent = messageComponent;
    }

    @Override
    public void setVariable(String key, @Nullable ITextComponent value) {
        if (outputVars == null) {
            outputVars = new HashMap<>();
        }

        outputVars.put(key, value);
    }

    @Override
    @Nullable
    public ITextComponent getVariable(String key) {
        return outputVars != null ? outputVars.get(key) : null;
    }
}
