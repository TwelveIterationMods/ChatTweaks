package net.blay09.mods.chattweaks.api.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PrintChatMessageEvent extends Event {

    private ITextComponent chatComponent;
    private int chatLineId;

    public PrintChatMessageEvent(ITextComponent chatComponent, int chatLineId) {
        this.chatComponent = chatComponent;
        this.chatLineId = chatLineId;
    }

    public ITextComponent getChatComponent() {
        return chatComponent;
    }

    public void setChatComponent(ITextComponent chatComponent) {
        this.chatComponent = chatComponent;
    }

    public int getChatLineId() {
        return chatLineId;
    }

    public void setChatLineId(int chatLineId) {
        this.chatLineId = chatLineId;
    }

}
