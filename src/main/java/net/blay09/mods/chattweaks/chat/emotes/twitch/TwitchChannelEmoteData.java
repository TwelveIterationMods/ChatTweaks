package net.blay09.mods.chattweaks.chat.emotes.twitch;

public class TwitchChannelEmoteData {
    private final int id;
    private final String channel;

    public TwitchChannelEmoteData(int id, String channel) {
        this.id = id;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }
}
