package net.blay09.mods.chattweaks.chat.emotes.twitch;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteSource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.URI;

public enum TwitchGlobalEmoteSource implements IEmoteSource<String> {
    INSTANCE;

    private static final String URL_TEMPLATE = "https://static-cdn.jtvnw.net/emoticons/v1/{{id}}/1.0";

    @Override
    public String getCacheFileName(String data) {
        return "twitch-" + data;
    }

    @Override
    public String getTooltip(String data) {
        return TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipTwitchEmotes");
    }

    @Override
    public void loadEmoteImage(IEmote<String> emote) throws Exception {
        ChatTweaksAPI.loadEmoteImage(emote, new URI(URL_TEMPLATE.replace("{{id}}", emote.getCustomData())));
    }

    @Override
    public boolean isCommonEmote(IEmote emote) {
        return true;
    }
}
