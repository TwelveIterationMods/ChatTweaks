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

    private static final List<ChatView> views = new ArrayList<>();
    private static final Map<String, ChatView> viewsByName = new HashMap<>();
    private static ChatView activeView;

    public static ChatView createDefaultView() {
        ChatView defaultView = new ChatViewImpl("*");
        defaultView.addChannel(ChatManager.mainChannel.getName());
        defaultView.addChannel(ChatManager.systemChannel.getName());
        defaultView.addChannel(ChatManager.deathChannel.getName());
        return defaultView;
    }

    public static ChatView createTestView() {
        ChatView testView = new ChatViewImpl("test");
        testView.addChannel(ChatManager.mainChannel.getName());
        testView.setFilterPattern(".+uwu.+");
        testView.setOutgoingPrefix("*rawr*, ");
        testView.setOutputFormat("${s}-wu: ${m}");
        testView.setExclusive(true);
        return testView;
    }

    public static ChatView createSystemView() {
        ChatView systemView = new ChatViewImpl("system");
        systemView.addChannel(ChatManager.systemChannel.getName());
        systemView.setDisplay(ChatManager.sideChatDisplay.getName());
        systemView.setExclusive(true);
        return systemView;
    }

    public static void registerView(ChatView chatView) {
        views.add(chatView);
        viewsByName.put(chatView.getName(), chatView);
    }

    public static List<ChatView> findChatViews(ChatMessage message, ChatChannel channel) {
        List<ChatView> result = new ArrayList<>();
        for (ChatView view : views) {
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
        return viewsByName.get(name);
    }

    public static void init() {
        registerView(createDefaultView());
        registerView(createTestView());
        registerView(createSystemView());
        activeView = views.get(0);
    }

    @SubscribeEvent
    public static void onClearChat(ClearChatEvent event) {
        // TODO clear messages from active view
        /*for (ChatChannel channel : ChatManager.getChatChannels()) {
            channel.clearChatMessages();
        }*/
    }

    public static List<ChatView> getChatViews() {
        return views;
    }
}
