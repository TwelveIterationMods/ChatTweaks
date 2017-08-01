package net.blay09.mods.chattweaks.chat.emotes;

public interface IEmoteLoader {
    /**
     * Should load the BufferedImage for the given emote and call setImage() on it.
     * This will be called from a separate thread, so don't call anything that requires a GL context here.
     * @param emote the emote being loaded
     */
    void loadEmoteImage(IEmote emote) throws Exception;

	default boolean isCommonEmote(String name) {
		return false;
	}
}
