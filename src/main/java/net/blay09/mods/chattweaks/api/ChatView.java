package net.blay09.mods.chattweaks.api;

public interface ChatView {
    String getName();

    boolean isMuted();

    void setExclusive(boolean exclusive);

    boolean isExclusive();

    String getDisplay();

    void setDisplay(String displayName);

    void addChannel(String channelName);

    boolean hasUnreadMessages();
    void markAsUnread();
    void markAsRead();

    boolean containsChannel(String channelName);

    boolean messageMatches(String message);
}
