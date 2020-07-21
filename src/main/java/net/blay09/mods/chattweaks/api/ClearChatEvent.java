package net.blay09.mods.chattweaks.api;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ClearChatEvent extends Event {
    private boolean clearSentMessageHistory;

    public ClearChatEvent(boolean clearSentMessageHistory) {
        this.clearSentMessageHistory = clearSentMessageHistory;
    }

    public boolean isClearSentMessageHistory() {
        return clearSentMessageHistory;
    }

    public void setClearSentMessageHistory(boolean clearSentMessageHistory) {
        this.clearSentMessageHistory = clearSentMessageHistory;
    }
}
