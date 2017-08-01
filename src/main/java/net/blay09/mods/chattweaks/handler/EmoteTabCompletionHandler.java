package net.blay09.mods.chattweaks.handler;

import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.event.TabCompletionEvent;
import net.minecraft.command.CommandBase;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class EmoteTabCompletionHandler {

	@SubscribeEvent
	public void onTabCompletion(TabCompletionEvent event) {
		if (event.getSide() == Side.CLIENT && ChatTweaksConfig.emoteTabCompletion) {
			event.getCompletions().addAll(CommandBase.getListOfStringsMatchingLastWord(new String[]{event.getInput()}, EmoteRegistry.getEmoteCodes()));
		}
	}
}
