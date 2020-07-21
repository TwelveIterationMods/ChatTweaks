package net.blay09.mods.chattweaks.api.event;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class ExtendedClientChatEvent extends ClientChatEvent {
    private String message;
    private boolean addToSentMessages;

    @Nullable
    private String historyOverride;

    public ExtendedClientChatEvent(String message, boolean addToSentMessages) {
        super(message);
        this.message = message;
        this.addToSentMessages = addToSentMessages;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAddToSentMessages() {
        return addToSentMessages;
    }

    public void setAddToSentMessages(boolean addToSentMessages) {
        this.addToSentMessages = addToSentMessages;
    }

    @Nullable
    public String getHistoryOverride() {
        return historyOverride;
    }

    public void setHistoryOverride(@Nullable String historyOverride) {
        this.historyOverride = historyOverride;
    }
}
