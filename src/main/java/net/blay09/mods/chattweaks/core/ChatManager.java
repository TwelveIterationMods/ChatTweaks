package net.blay09.mods.chattweaks.core;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.ChatChannel;
import net.blay09.mods.chattweaks.api.ChatDisplay;
import net.blay09.mods.chattweaks.api.ChatMessage;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.api.event.PrintChatMessageEvent;
import net.blay09.mods.chattweaks.bottombar.BottomChatDisplay;
import net.blay09.mods.chattweaks.sidebar.SideChatDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class ChatManager {

    public static final ChatChannel mainChannel = new ChatChannelImpl("main", "Default", new ResourceLocation("chattweaks:textures/channel_main.png"), null);

    private static final String[] systemLang = new String[]{
            "gameMode.changed",
            "commands.time.set",
            "chat.type.admin",
    };
    public static final ChatChannel systemChannel = new ChatChannelImpl("system", "e.g. command response", new ResourceLocation("chattweaks:textures/channel_system.png"), key -> ArrayUtils.contains(systemLang, key));

    public static final ChatChannel deathChannel = new ChatChannelImpl("death", "death messages", new ResourceLocation("chattweaks:textures/channel_death.png"), key -> key.startsWith("death."));

    public static final BottomChatDisplay bottomChatDisplay = new BottomChatDisplay();
    public static final SideChatDisplay sideChatDisplay = new SideChatDisplay();

    private static final AtomicInteger chatLineIdCounter = new AtomicInteger(0);
    private static final Map<String, ChatChannel> channels = new HashMap<>();
    private static final Map<String, ChatDisplay> displays = new HashMap<>();

    public static void init() {
        addChatChannel(mainChannel);
        addChatChannel(systemChannel);
        addChatChannel(deathChannel);

        addChatDisplay(bottomChatDisplay);
        addChatDisplay(sideChatDisplay);
    }

    public static void addChatDisplay(ChatDisplay display) {
        displays.put(display.getName(), display);
    }

    public static void addChatChannel(ChatChannel channel) {
        channels.put(channel.getName(), channel);
    }

    public static Collection<ChatChannel> getChatChannels() {
        return channels.values();
    }

    public static void removeChatLine(int chatLineId) {
        for (ChatChannel channel : channels.values()) {
            channel.removeChatMessage(chatLineId);
        }
    }

    public static void removeChatChannel(String name) {
        ChatChannel channel = channels.remove(name);
        if (channel != null) {
            for (ChatView chatView : ChatViewManager.getViews()) {
                chatView.removeChannel(channel.getName());
            }
        }
    }

    @SubscribeEvent
    public static void onPrintChatMessage(PrintChatMessageEvent event) {
        int chatLineId = event.getChatLineId();
        if (chatLineId == 0) {
            chatLineId = getNextMessageId();
        }

        ChatMessage message = new ChatMessageImpl(chatLineId, event.getChatComponent());
        addChatMessage(message, findChatChannel(message));
        event.setCanceled(true);
    }

    public static int getNextMessageId() {
        return chatLineIdCounter.incrementAndGet();
    }

    public static void addChatMessage(ChatMessage message, ChatChannel channel) {
        channel.addChatMessage(message);

        List<ChatView> views = ChatViewManager.findChatViews(message, channel);
        boolean hasReadMessage = views.contains(ChatViewManager.getActiveView());
        for (ChatView view : views) {
            ChatMessage viewMessage = view.addChatLine(message);
            if (!hasReadMessage) {
                view.markAsUnread();
            }
            addChatMessageForDisplay(viewMessage, view);
        }
    }

    public static ChatChannel findChatChannel(ChatMessage message) {
        if (message.getTextComponent() instanceof TranslationTextComponent) {
            String key = ((TranslationTextComponent) message.getTextComponent()).getKey();
            if (!key.equals("chat.type.text") && !key.equals("chat.type.emote")) {
                for (ChatChannel channel : channels.values()) {
                    if (channel.getLangKeyMatcher() != null && channel.getLangKeyMatcher().test(key)) {
                        return channel;
                    }
                }
            }
        }

        return mainChannel;
    }

    @Nullable
    public static ChatChannel getChatChannel(String name) {
        return channels.get(name);
    }

    private static void addChatMessageForDisplay(ChatMessage chatMessage, ChatView view) {
        final ChatDisplay display = displays.getOrDefault(view.getDisplay(), displays.get("chat"));
        display.addChatMessage(chatMessage, view);
    }
}
