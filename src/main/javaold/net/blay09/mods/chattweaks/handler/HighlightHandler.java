package net.blay09.mods.chattweaks.handler;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.event.PrintChatMessageEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID)
public class HighlightHandler {

	@SubscribeEvent
	public static void onPrintChatMessage(PrintChatMessageEvent event) {
		final boolean shouldHighlightName = ChatTweaksConfig.CLIENT.highlightName.get();
		final List<? extends String> highlightedWords = ChatTweaksConfig.CLIENT.highlightWords.get();
		if(!shouldHighlightName && highlightedWords.isEmpty()) {
			return;
		}

		ITextComponent senderComponent = event.getChatMessage().getSender();
		String sender = senderComponent != null ? TextFormatting.getTextWithoutFormattingCodes(senderComponent.getString()) : null;
		if(sender != null && !sender.isEmpty()) {
			PlayerEntity player = Minecraft.getInstance().player;
			String playerName = player != null ? TextFormatting.getTextWithoutFormattingCodes(player.getDisplayName().getString()) : null;
			if(!sender.equals(playerName)) {
				ITextComponent messageComponent = event.getChatMessage().getMessage();
				if(messageComponent != null) {
					String message = messageComponent.getString();
					if (shouldHighlightName && message.matches(".*(?:[\\p{Punct} ]|^)" + playerName + "(?:[\\p{Punct} ]|$).*")) {
						event.getChatMessage().setBackgroundColor(ChatTweaksConfig.backgroundColorHighlight);
					} else {
						for (String highlight : highlightedWords) {
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
