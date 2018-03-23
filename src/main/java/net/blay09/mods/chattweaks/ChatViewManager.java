package net.blay09.mods.chattweaks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.blay09.mods.chattweaks.chat.ChatMessage;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.chat.MessageStyle;
import net.blay09.mods.chattweaks.gui.chat.GuiChatExt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChatViewManager {

    private static final Map<String, ChatView> views = Maps.newHashMap();
    private static final List<ChatView> sortedViews = Lists.newArrayList();
    private static String[] viewNames;
    private static String[] tabViewNames;
    private static ChatView activeView;

    public static ChatView createDefaultView() {
        ChatView defaultView = new ChatView("*");
        defaultView.addChannel(ChatManager.mainChannel);
        defaultView.addChannel(ChatManager.interactionChannel);
        defaultView.addChannel(ChatManager.systemChannel);
        defaultView.addChannel(ChatManager.deathChannel);
        return defaultView;
    }

    public static ChatView createSystemView() {
        ChatView systemView = new ChatView("system");
        systemView.addChannel(ChatManager.systemChannel);
        systemView.setMessageStyle(MessageStyle.Side);
        systemView.setExclusive(true);
        return systemView;
    }

    public static ChatView createInteractionView() {
        ChatView interactionView = new ChatView("interaction");
        interactionView.addChannel(ChatManager.interactionChannel);
        interactionView.setMessageStyle(MessageStyle.Bottom);
        interactionView.setExclusive(true);
        return interactionView;
    }

    public static ChatView[] createDefaults() {
        return new ChatView[]{
                createDefaultView(),
                createSystemView(),
                createInteractionView()
        };
    }

    public static void load() {
        removeAllChatViews();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "config/ChatTweaks/views.json"))) {
            JsonObject root = gson.fromJson(reader, JsonObject.class);
            JsonArray jsonViews = root.getAsJsonArray("views");
            for (int i = 0; i < jsonViews.size(); i++) {
                addChatView(ChatView.fromJson(jsonViews.get(i).getAsJsonObject()));
            }
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            ChatTweaks.logger.error("An error occurred trying to load the chat views: ", e);
        }
        if (views.isEmpty()) {
            addChatView(createDefaultView());
            addChatView(createSystemView());
            addChatView(createInteractionView());
        }
        setActiveView(getNextChatView(null, false));
    }

    public static void save() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/ChatTweaks/views.json"))) {
            JsonWriter jsonWriter = new JsonWriter(writer);
            jsonWriter.setIndent("  ");
            JsonObject root = new JsonObject();
            JsonArray jsonViews = new JsonArray();
            for (ChatView view : sortedViews) {
                if (!view.isTemporary()) {
                    jsonViews.add(view.toJson());
                }
            }
            root.add("views", jsonViews);
            gson.toJson(root, jsonWriter);
        } catch (IOException e) {
            ChatTweaks.logger.error("An error occurred trying to save the chat views: ", e);
        }
    }

    private static void updateNameCache() {
        viewNames = sortedViews.stream().map(ChatView::getName).toArray(String[]::new);
        tabViewNames = sortedViews.stream().filter(p -> p.getMessageStyle() == MessageStyle.Chat).map(ChatView::getName).toArray(String[]::new);
    }

    public static void addChatView(ChatView view) {
        String origName = view.getName();
        int c = 1;
        while (views.containsKey(view.getName())) {
            view.setName(origName + " (" + c + ")");
            c++;
        }
        views.put(view.getName(), view);
        sortedViews.add(view);
        updateNameCache();

        GuiScreen gui = Minecraft.getMinecraft().currentScreen;
        if (gui instanceof GuiChatExt) {
            ((GuiChatExt) gui).updateChannelButtons();
        }
    }

    public static void removeChatView(ChatView view) {
        views.remove(view.getName());
        sortedViews.remove(view);
        if (views.isEmpty()) {
            ChatView defaultView = createDefaultView();
            views.put("*", defaultView);
            sortedViews.add(defaultView);
        }
        updateNameCache();
        if (view == activeView) {
            setActiveView(getNextChatView(view, false));
        }
    }

    public static ChatView getNextChatView(@Nullable ChatView view, boolean preferNewMessages) {
        if (preferNewMessages) {
            for (ChatView chatView : sortedViews) {
                if (chatView != view && chatView.hasUnreadMessages()) {
                    return chatView;
                }
            }
        }
        String[] arr = tabViewNames;
        if (arr.length == 0) {
            arr = viewNames;
        }
        int index = -1;
        if (view != null) {
            index = ArrayUtils.indexOf(arr, view.getName());
        }
        index++;
        if (index >= arr.length) {
            index = 0;
        }
        return views.get(arr[index]);
    }

    public static List<ChatView> findChatViews(ChatMessage message, ChatChannel channel) {
        String unformattedText = message.getTextComponent().getUnformattedText();
        List<ChatView> result = Lists.newArrayList();
        for (ChatView view : sortedViews) {
            if (view.getChannels().contains(channel) && view.messageMatches(unformattedText)) {
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
        view.markAsUnread(false);
        ChatTweaks.getChatDisplay().refreshChat();
    }

    public static ChatView getActiveView() {
        return activeView;
    }

    public static Collection<ChatView> getViews() {
        return sortedViews;
    }

    @Nullable
    public static ChatView getChatView(String name) {
        return views.get(name);
    }

    @Deprecated
    public static ChatView getOrCreateChatView(String name) {
        ChatView chatView = getChatView(name);
        if (chatView == null) {
            chatView = new ChatView(name);
            addChatView(chatView);
        }
        return chatView;
    }

    private static List<String> reservedNames = Lists.newArrayList();

    public static String getFreeChatViewName() {
        String baseName = "New View";
        String name = baseName;
        int i = 0;
        while (views.containsKey(name) || reservedNames.contains(name)) {
            i++;
            name = baseName + " (" + i + ")";
        }
        reservedNames.add(name);
        return name;
    }

    public static void removeAllChatViews() {
        views.clear();
        sortedViews.clear();
        viewNames = new String[0];
        tabViewNames = new String[0];
        reservedNames.clear();
    }

    public static void renameChatView(ChatView chatView, String name) {
        views.remove(chatView.getName());
        sortedViews.remove(chatView);
        chatView.setName(name);
        addChatView(chatView);
    }

}
