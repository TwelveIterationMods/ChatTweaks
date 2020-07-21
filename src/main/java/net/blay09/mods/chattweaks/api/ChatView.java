package net.blay09.mods.chattweaks.api;

import java.util.Collection;

public interface ChatView {
    String getName();

    boolean isMuted();

    void setMuted(boolean muted);

    void setExclusive(boolean exclusive);

    boolean isExclusive();

    String getDisplay();

    void setDisplay(String displayName);

    void addChannel(String channelName);

    boolean containsChannel(String channelName);

    void removeChannel(String channelName);

    boolean hasUnreadMessages();

    void markAsUnread();

    void markAsRead();

    boolean matchesFilter(ChatMessage chatMessage);

    ChatMessage addChatMessage(ChatMessage chatMessage);

    Collection<ChatMessage> getChatMessages();

    String getFilterPattern();

    void setFilterPattern(String filterPattern);

    String getOutputFormat();

    void setOutputFormat(String outputFormat);

    String getOutgoingPrefix();

    void setOutgoingPrefix(String outgoingPrefix);

    void refresh();
}
