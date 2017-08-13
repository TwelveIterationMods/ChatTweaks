package net.blay09.mods.chattweaks.handler;

import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.event.PrintChatMessageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HighlightHandler {

	@SubscribeEvent
	public void onPrintChatMessage(PrintChatMessageEvent event) {
		if(!ChatTweaksConfig.highlightName && ChatTweaksConfig.highlightStrings.length == 0) {
			return;
		}
		ITextComponent senderComponent = event.getChatMessage().getSender();
		String sender = senderComponent != null ? TextFormatting.getTextWithoutFormattingCodes(senderComponent.getUnformattedText()) : null;
		if(sender != null) {
			EntityPlayer player = Minecraft.getMinecraft().player;
			String playerName = player != null ? player.getDisplayNameString() : null;
			if(!sender.equals(playerName)) {
				ITextComponent messageComponent = event.getChatMessage().getMessage();
				if(messageComponent != null) {
					String message = messageComponent.getUnformattedText();
					if (ChatTweaksConfig.highlightName && message.matches(".*(?:[\\p{Punct} ]|^)" + playerName + "(?:[\\p{Punct} ]|$).*")) {
						event.getChatMessage().setBackgroundColor(ChatTweaksConfig.backgroundColorHighlight);
					} else {
						for (String highlight : ChatTweaksConfig.highlightStrings) {
							if (message.contains(highlight)) {
								event.getChatMessage().setBackgroundColor(ChatTweaksConfig.backgroundColorHighlight);
								break;
							}
						}
					}
				}
			}
		}
	}

}
