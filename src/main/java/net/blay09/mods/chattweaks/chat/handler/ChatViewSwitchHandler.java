package net.blay09.mods.chattweaks.chat.handler;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.ModKeyBindings;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.chat.ExtendedNewChatGui;
import net.blay09.mods.chattweaks.chat.screen.ExtendedChatScreen;
import net.blay09.mods.chattweaks.chat.widget.ChatViewButton;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID)
public class ChatViewSwitchHandler {

    @SubscribeEvent
    public static void onInitGui(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() instanceof ExtendedChatScreen) {
            final FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            final List<ChatView> chatViews = getChatDisplayViews();
            if (chatViews.size() > 1) {
                int x = 2;
                for (ChatView chatView : chatViews) {
                    final ChatViewButton button = new ChatViewButton(x, event.getGui().height - 25, fontRenderer, chatView);
                    event.addWidget(button);
                    x += button.getWidth() + 2;
                }
            }
        }
    }

    private static List<ChatView> getChatDisplayViews() {
        return ChatViewManager.getChatViews().stream().filter(it -> it.getDisplay().equals(ExtendedNewChatGui.DISPLAY_NAME)).collect(Collectors.toList());
    }

    @SubscribeEvent
    public static void onKeyboard(GuiScreenEvent.KeyboardKeyPressedEvent e) {
        final InputMappings.Input input = InputMappings.getInputByCode(e.getKeyCode(), e.getScanCode());
        if (ModKeyBindings.keySwitchChatView.isActiveAndMatches(input)) {
            final ChatView nextChatView = getNextChatView(ChatViewManager.getActiveView(), ChatTweaksConfig.CLIENT.smartViewNavigation.get());
            if (nextChatView != null) {
                ChatViewManager.setActiveView(nextChatView);
            }
            e.setCanceled(true);
        }
    }

    @Nullable
    public static ChatView getNextChatView(@Nullable ChatView currentView, boolean preferViewWithNewMessages) {
        final List<ChatView> views = getChatDisplayViews();
        if (views.isEmpty()) {
            return null;
        }

        if (preferViewWithNewMessages) {
            for (ChatView chatView : views) {
                if (chatView != currentView && chatView.hasUnreadMessages()) {
                    return chatView;
                }
            }
        }

        int index = -1;
        if (currentView != null) {
            index = views.indexOf(currentView);
        }

        if (index + 1 >= views.size()) {
            index = -1;
        }

        return views.get(index + 1);
    }

}
