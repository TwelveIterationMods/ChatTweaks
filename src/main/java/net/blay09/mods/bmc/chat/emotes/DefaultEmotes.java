package net.blay09.mods.bmc.chat.emotes;

import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;
import net.blay09.mods.bmc.api.emote.IEmoteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

public class DefaultEmotes implements IEmoteLoader {

	public DefaultEmotes(String... emotes) {
		IEmoteGroup group = BetterMinecraftChatAPI.registerEmoteGroup("Default");
		for(String code : emotes) {
			IEmote emote = BetterMinecraftChatAPI.registerEmote(code, this);
			emote.addTooltip(TextFormatting.GRAY + "Default Emotes");
			emote.setCustomData(new ResourceLocation(BetterMinecraftChat.MOD_ID, "emotes/" + code + ".png"));
			group.addEmote(emote);
		}
	}

	@Override
	public void loadEmoteImage(IEmote emote) {
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource((ResourceLocation) emote.getCustomData());
			if(resource != null) {
				BetterMinecraftChatAPI.loadEmoteImage(emote, resource.getInputStream());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
