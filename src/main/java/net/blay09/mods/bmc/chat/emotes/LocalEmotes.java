package net.blay09.mods.bmc.chat.emotes;

import com.google.common.io.Files;
import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksAPI;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class LocalEmotes implements IEmoteLoader {

	public LocalEmotes(File directory) {
		//noinspection ResultOfMethodCallIgnored
		directory.mkdirs();
		IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("Local");
		File[] files = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png") || name.endsWith(".gif");
			}
		});
		if(files != null) {
			for(File file : files) {
				IEmote emote = ChatTweaksAPI.registerEmote(Files.getNameWithoutExtension(file.getName()), this);
				emote.addTooltip(TextFormatting.GRAY + I18n.format(ChatTweaks.MOD_ID + ":gui.chat.tooltipLocalEmotes"));
				emote.setCustomData(file);
				group.addEmote(emote);
			}
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			ChatTweaksAPI.loadEmoteImage(emote, ((File) emote.getCustomData()).toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
