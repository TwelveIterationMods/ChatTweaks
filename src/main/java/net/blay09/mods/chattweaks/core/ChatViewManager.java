package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.ChatChannel;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.api.ClearChatEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class ChatViewManager {

    private static final Map<String, ChatView> views = new HashMap<>();
    private static ChatView activeView;

    public static ChatView createDefaultView() {
        ChatView defaultView = new ChatViewImpl("*");
        defaultView.addChannel(ChatManager.mainChannel.getName());
        defaultView.addChannel(ChatManager.systemChannel.getName());
        defaultView.addChannel(ChatManager.deathChannel.getName());
        return defaultView;
    }

    public static ChatView createSystemView() {
        ChatView systemView = new ChatViewImpl("system");
        systemView.addChannel(ChatManager.systemChannel.getName());
        systemView.setDisplay(ChatManager.sideChatDisplay.getName());
        systemView.setExclusive(true);
        return systemView;
    }

    public static List<ChatView> findChatViews(ChatMessage message, ChatChannel channel) {
        List<ChatView> result = new ArrayList<>();
        for (ChatView view : views.values()) {
            if (view.containsChannel(channel.getName()) && view.matchesFilter(message)) {
                if (view.isExclusive()) {
                    result.clear();
                    result.add(view);
                    message.setExclusiveView(view);
                    break;
                }
                result.add(view);
            }
        }

        return result;
    }

    public static void setActiveView(ChatView view) {
        activeView = view;
        view.markAsRead();
        // TODO ChatTweaks.getChatDisplay().refreshChat();
    }

    public static ChatView getActiveView() {
        return activeView;
    }

    @Nullable
    public static ChatView getChatView(String name) {
        return views.get(name);
    }

    public static void init() {
        views.put("*", createDefaultView());
        views.put("system", createSystemView());
        activeView = views.get("*");
    }

    @SubscribeEvent
    public static void onClearChat(ClearChatEvent event) {
        // TODO clear messages from active view
        /*for (ChatChannel channel : ChatManager.getChatChannels()) {
            channel.clearChatMessages();
        }*/
    }

    public static Collection<ChatView> getViews() {
        return views.values();
    }
}
