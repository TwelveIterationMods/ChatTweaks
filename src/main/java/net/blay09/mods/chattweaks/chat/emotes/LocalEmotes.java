package net.blay09.mods.chattweaks.chat.emotes;

import com.google.common.io.Files;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksAPI;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.File;

public class LocalEmotes implements IEmoteLoader {

    public LocalEmotes(File directory) throws Exception {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new Exception("Could not create local emotes directory.");
        }
        IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("Local");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".gif"));
        if (files != null) {
            for (File file : files) {
                IEmote emote = ChatTweaksAPI.registerEmote(Files.getNameWithoutExtension(file.getName()), this);
                emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipLocalEmotes"));
                emote.setCustomData(file);
                group.addEmote(emote);
            }
        }

        for (String aliasMapping : ChatTweaksConfig.localEmoteAliases) {
            int lastEq = aliasMapping.lastIndexOf('=');
            if (lastEq == -1 || lastEq == 0 || lastEq == aliasMapping.length() - 1) {
                System.err.println("Skipping local emote alias " + aliasMapping + " due to invalid format.");
                continue;
            }

            String alias = aliasMapping.substring(0, lastEq);
            String fileName = aliasMapping.substring(lastEq + 1);
            File file = new File(directory, fileName);
            if (!file.exists()) {
                System.err.println("Skipping local emote alias " + aliasMapping + " due to missing file.");
                continue;
            }

            IEmote emote = ChatTweaksAPI.registerEmote(alias, this);
            emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipLocalEmotes"));
            emote.setCustomData(file);
            group.addEmote(emote);
        }
    }

    @Override
    public void loadEmoteImage(IEmote emote) throws Exception {
        ChatTweaksAPI.loadEmoteImage(emote, ((File) emote.getCustomData()).toURI());
    }

}
