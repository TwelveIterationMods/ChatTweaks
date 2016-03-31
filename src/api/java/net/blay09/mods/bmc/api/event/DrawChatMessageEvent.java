package net.blay09.mods.bmc.api.event;

import net.minecraft.client.gui.ChatLine;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class DrawChatMessageEvent extends Event {

    private final ChatLine chatLine;
    private final String formattedText;
    private final int x;
    private final int y;
    private final int alpha;

    public DrawChatMessageEvent(ChatLine chatLine, String formattedText, int x, int y, int alpha) {
        this.chatLine = chatLine;
        this.formattedText = formattedText;
        this.x = x;
        this.y = y;
        this.alpha = alpha;
    }

    @Cancelable
    public static class Pre extends DrawChatMessageEvent {
        public Pre(ChatLine chatLine, String formattedText, int x, int y, int alpha) {
            super(chatLine, formattedText, x, y, alpha);
        }
    }

    public static class Post extends DrawChatMessageEvent {
        public Post(ChatLine chatLine, String formattedText, int x, int y, int alpha) {
            super(chatLine, formattedText, x, y, alpha);
        }
    }

    public ChatLine getChatLine() {
        return chatLine;
    }

    public String getFormattedText() {
        return formattedText;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getAlpha() {
        return alpha;
    }
}
