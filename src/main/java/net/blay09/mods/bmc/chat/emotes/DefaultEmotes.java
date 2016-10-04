package net.blay09.mods.bmc.chat.emotes;

import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.ChatTweaksAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class DefaultEmotes implements IEmoteLoader {

	public DefaultEmotes(String... emotes) {
		IEmoteGroup group = ChatTweaksAPI.registerEmoteGroup("Default");
		for(String code : emotes) {
			IEmote emote = ChatTweaksAPI.registerEmote(code, this);
			emote.addTooltip(TextFormatting.GRAY + "Default Emotes");
			emote.setCustomData(new ResourceLocation(ChatTweaks.MOD_ID, "emotes/" + code + ".png"));
			group.addEmote(emote);
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource((ResourceLocation) emote.getCustomData());
			ChatTweaksAPI.loadEmoteImage(emote, resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
