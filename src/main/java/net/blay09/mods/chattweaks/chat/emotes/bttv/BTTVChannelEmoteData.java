package net.blay09.mods.chattweaks.chat.emotes.bttv;

public class BTTVChannelEmoteData {
    private final String id;
    private final String channel;

    public BTTVChannelEmoteData(String id, String channel) {
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
