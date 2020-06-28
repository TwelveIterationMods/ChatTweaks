package net.blay09.mods.chattweaks.chat.emotes.twitch;

public class TwitchChannelEmoteData {
    private final String id;
    private final String channel;

    public TwitchChannelEmoteData(String id, String channel) {
        this.id = id;
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public String getChannel() {
        return channel;
    }
}
