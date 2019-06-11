package net.blay09.mods.chattweaks.chat.emotes.ffz;

public class FFZChannelEmoteData {
    private final String id;
    private final String url;
    private final String channel;

    public FFZChannelEmoteData(String id, String url, String channel) {
        this.id = id;
        this.url = url;
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getChannel() {
        return channel;
    }
}
