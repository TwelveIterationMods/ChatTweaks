package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.api.ChatView;

import java.util.HashSet;
import java.util.Set;

public class ChatViewImpl implements ChatView {

    private final Set<String> channels = new HashSet<>();

    private final String name;
    private String display;
    private boolean exclusive;
    private boolean unread;

    public ChatViewImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isMuted() {
        return false;
    }

    @Override
    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    @Override
    public boolean isExclusive() {
        return exclusive;
    }

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public void addChannel(String channelName) {
        channels.add(channelName);
    }

    @Override
    public boolean hasUnreadMessages() {
        return unread;
    }

    @Override
    public void markAsUnread() {
        unread = true;
    }

    @Override
    public void markAsRead() {
        unread = false;
    }

    @Override
    public boolean containsChannel(String channelName) {
        return channels.contains(channelName);
    }

    @Override
    public boolean messageMatches(String message) {
        return true;
    }
}
