package net.blay09.mods.chattweaks.chat.handler;

public class ChatViewSwitchHandler {
    /*

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

	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState() && ChatTweaks.keySwitchChatView.isActiveAndMatches(Keyboard.getEventKey())) {
			ChatViewManager.setActiveView(ChatViewManager.getNextChatView(ChatViewManager.getActiveView(), ChatTweaksConfig.CLIENT.smartViewNavigation.get()));
		} else {
			super.handleKeyboardInput();
		}
	}

    public void updateChannelButtons() {
		buttonList.removeIf(p -> p instanceof ChatViewButton);
		if (ChatViewManager.getViews().size() > 1) {
			int x = 2;
			int y = height - 25;
			for (ChatView chatView : ChatViewManager.getViews()) {
				if (chatView.getMessageStyle() != MessageStyle.Chat) {
					continue;
				}
				ChatViewButton btnChatView = new ChatViewButton(-1, x, y, Minecraft.getInstance().fontRenderer, chatView);
				buttonList.add(btnChatView);
				x += btnChatView.width + 2;
			}
		}
	}

    } else if (button instanceof ChatViewButton) {
			ChatViewManager.setActiveView(((ChatViewButton) button).getView());
		}
     */
}
