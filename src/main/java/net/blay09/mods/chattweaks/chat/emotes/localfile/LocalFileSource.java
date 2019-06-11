package net.blay09.mods.chattweaks.chat.emotes.localfile;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.chat.emotes.IEmote;
import net.blay09.mods.chattweaks.chat.emotes.IEmoteSource;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.File;

public enum LocalFileSource implements IEmoteSource<File> {
    INSTANCE;

    @Override
    public String getCacheFileName(File data) {
        return data.getName();
    }

    @Override
    public String getTooltip(File data) {
        return TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipLocalEmotes");
    }

    @Override
    public void loadEmoteImage(IEmote<File> emote) throws Exception {
        ChatTweaksAPI.loadEmoteImage(emote, emote.getCustomData().toURI());
    }


}
