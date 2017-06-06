package net.blay09.mods.bmc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.blay09.mods.bmc.chat.ChatView;
import net.blay09.mods.bmc.chat.MessageStyle;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ChatViewManager {

	private static final Map<String, ChatView> views = Maps.newHashMap();
	private static String[] viewNames;
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
		return new ChatView[] {
				createDefaultView(),
				createSystemView(),
				createInteractionView()
		};
	}

	public static void load() {
		removeAllChatViews();
		Gson gson = new Gson();
		try(FileReader reader = new FileReader(new File(Minecraft.getMinecraft().mcDataDir, "config/ChatTweaks/views.json"))) {
			JsonObject root = gson.fromJson(reader, JsonObject.class);
			JsonArray jsonViews = root.getAsJsonArray("views");
			for(int i = 0; i < jsonViews.size(); i++) {
				addChatView(ChatView.fromJson(jsonViews.get(i).getAsJsonObject()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(views.isEmpty()) {
			addChatView(createDefaultView());
			addChatView(createSystemView());
			addChatView(createInteractionView());
		}
	}

	public static void save() {
		Gson gson = new Gson();
		try(FileWriter writer = new FileWriter(new File(Minecraft.getMinecraft().mcDataDir, "config/ChatTweaks/views.json"))) {
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setIndent("  ");
			JsonObject root = new JsonObject();
			JsonArray jsonViews = new JsonArray();
			for(ChatView view : views.values()) {
				jsonViews.add(view.toJson());
			}
			root.add("views", jsonViews);
			gson.toJson(root, jsonWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addChatView(ChatView view) {
		if(views.containsKey(view.getName())) {
			throw new IllegalArgumentException("duplicate view " + view.getName());
		}
		views.put(view.getName(), view);
		viewNames = views.keySet().toArray(new String[views.keySet().size()]);
	}

	public static void removeChatView(ChatView view) {
		views.remove(view.getName());
		if(views.isEmpty()) {
			views.put("*", createDefaultView());
		}
		viewNames = views.keySet().toArray(new String[views.keySet().size()]);
		if(view == activeView) {
			setActiveView(getNextChatView(view));
		}
	}

	public static ChatView getNextChatView(@Nullable ChatView view) {
		int index = -1;
		if(view != null) {
			index = ArrayUtils.indexOf(viewNames, view.getName());
		}
		index++;
		if(index >= viewNames.length) {
			index = 0;
		}
		return views.get(viewNames[index]);
	}

	public static List<ChatView> findChatViews(ChatMessage message, ChatChannel channel) {
		String unformattedText = message.getTextComponent().getUnformattedText();
		List<ChatView> result = Lists.newArrayList();
		for (ChatView view : views.values()) {
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
		ChatTweaks.getChatDisplay().refreshChat();
	}

	public static ChatView getActiveView() {
		return activeView;
	}

	public static Collection<ChatView> getViews() {
		return views.values();
	}

	@Nullable
	public static ChatView getChatView(String name) {
		return views.get(name);
	}

	public static ChatView getOrCreateChatView(String name) {
		return views.computeIfAbsent(name, ChatView::new);
	}

	private static List<String> reservedNames = Lists.newArrayList();
	public static String getNextChatViewName() {
		String baseName = "New View";
		String name = baseName;
		int i = 0;
		while(views.containsKey(name) || reservedNames.contains(name)) {
			i++;
			name = baseName + " (" + i + ")";
		}
		reservedNames.add(name);
		return name;
	}

	public static void removeAllChatViews() {
		views.clear();
		viewNames = new String[0];
		reservedNames.clear();
	}

	public static void renameChatView(ChatView chatView, String name) {
		views.remove(chatView.getName());
		chatView.setName(name);
		addChatView(chatView);
	}
}
