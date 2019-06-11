package net.blay09.mods.chattweaks.chat.emotes.bttv;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteSource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.net.URI;

public enum BTTVEmoteSource implements IEmoteSource<String> {
    INSTANCE;

    private String urlTemplate;

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    @Override
    public String getCacheFileName(String data) {
        return "bttv-" + data;
    }

    @Override
    public String getTooltip(String data) {
        return TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipBTTVEmotes");
    }

    @Override
    public void loadEmoteImage(IEmote<String> emote) throws Exception {
        ChatTweaksAPI.loadEmoteImage(emote, new URI("https:" + urlTemplate.replace("{{id}}", emote.getCustomData()).replace("{{image}}", "1x")));
    }
}
