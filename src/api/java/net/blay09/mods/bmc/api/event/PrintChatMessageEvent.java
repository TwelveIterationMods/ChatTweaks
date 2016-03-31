package net.blay09.mods.bmc.api.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PrintChatMessageEvent extends Event {

    private ITextComponent message;
    private int chatLineId;

    public PrintChatMessageEvent(ITextComponent message, int chatLineId) {
        this.message = message;
        this.chatLineId = chatLineId;
    }

    public ITextComponent getMessage() {
        return message;
    }

    public void setMessage(ITextComponent message) {
        this.message = message;
    }

    public int getChatLineId() {
        return chatLineId;
    }

    public void setChatLineId(int chatLineId) {
        this.chatLineId = chatLineId;
    }

}
