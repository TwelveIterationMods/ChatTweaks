package net.blay09.mods.chattweaks.chat.emotes;

public interface IEmoteSource<T> {
    String getCacheFileName(T data);

    String getTooltip(T data);

    /**
     * Should load the BufferedImage for the given emote and call setImage() on it.
     * This will be called from a separate thread, so don't call anything that requires a GL context here.
     *
     * @param emote the emote being loaded
     */
    void loadEmoteImage(IEmote<T> emote) throws Exception;

    default boolean isCommonEmote(IEmote emote) {
        return false;
    }
}
