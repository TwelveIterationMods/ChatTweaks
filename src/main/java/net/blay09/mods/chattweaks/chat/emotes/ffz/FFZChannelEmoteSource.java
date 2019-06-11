package net.blay09.mods.chattweaks.chat.emotes.ffz;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteSource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.URI;

public enum FFZChannelEmoteSource implements IEmoteSource<FFZChannelEmoteData> {
    INSTANCE;

    @Override
    public String getCacheFileName(FFZChannelEmoteData data) {
        return "ffz-" + data.getId();
    }

    @Override
    public String getTooltip(FFZChannelEmoteData data) {
        return TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipEmoteChannel") + " " + data.getChannel();
    }

    @Override
    public void loadEmoteImage(IEmote<FFZChannelEmoteData> emote) throws Exception {
        ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + emote.getCustomData().getUrl()));
    }


}
