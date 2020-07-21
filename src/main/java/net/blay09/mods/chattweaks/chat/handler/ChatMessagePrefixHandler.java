package net.blay09.mods.chattweaks.chat.handler;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.api.event.ExtendedClientChatEvent;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class ChatMessagePrefixHandler {

    @SubscribeEvent
    public static void onClientChat(ExtendedClientChatEvent event) {
        final ChatView activeView = ChatViewManager.getActiveView();
        if (activeView.getOutgoingPrefix() != null && !(event.getMessage().startsWith("/") && !event.getMessage().startsWith("/me "))) {
            event.setHistoryOverride(event.getMessage());
            event.setMessage(activeView.getOutgoingPrefix() + event.getMessage());
        }
    }

}
