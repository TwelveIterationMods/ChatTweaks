package net.blay09.mods.chattweaks.gui.screen;

import com.google.common.base.Strings;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.ChatViewManager;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.chat.MessageStyle;
import net.blay09.mods.chattweaks.event.ChatComponentClickEvent;
import net.blay09.mods.chattweaks.event.ChatComponentHoverEvent;
import net.blay09.mods.chattweaks.event.ClientChatEvent;
import net.blay09.mods.chattweaks.event.TabCompletionEvent;
import net.blay09.mods.chattweaks.gui.widget.ChatViewButton;
import net.blay09.mods.chattweaks.gui.widget.ChatSettingsButton;
import net.blay09.mods.chattweaks.imagepreview.ImageUrlTransformers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ExtendedChatScreen extends ChatScreen {

	public ExtendedChatScreen(String defaultText) {
		super(defaultText);
	}

	@Override
	public void initGui() {
		String oldText = inputField != null ? inputField.getText() : null;
		super.initGui();
		inputField.width = inputField.width - 36;
		if (!Strings.isNullOrEmpty(oldText)) {
			inputField.setText(oldText);
		}

		buttonList.add(new ChatSettingsButton(0, width - 16, height - 14));

		updateChannelButtons();
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

	@Override
	protected void actionPerformed(Button button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof ChatSettingsButton) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				mc.displayGuiScreen(new GuiChatView(null, ChatViewManager.getActiveView()));
			} else {
				mc.displayGuiScreen(new GuiFactory.ConfigGUI(this));
			}
		} else if (button instanceof ChatViewButton) {
			ChatViewManager.setActiveView(((ChatViewButton) button).getView());
		}
	}

	@Override
	public void sendChatMessage(String message, boolean addToSentMessages) {
		if (ChatViewManager.getActiveView().getOutgoingPrefix() != null && !(event.getMessage().startsWith("/") && !event.getMessage().startsWith("/me "))) {
			event.setMessage(ChatViewManager.getActiveView().getOutgoingPrefix() + event.getMessage());
		}
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if (Keyboard.getEventKeyState() && ChatTweaks.keySwitchChatView.isActiveAndMatches(Keyboard.getEventKey())) {
			ChatViewManager.setActiveView(ChatViewManager.getNextChatView(ChatViewManager.getActiveView(), ChatTweaksConfig.CLIENT.smartViewNavigation.get()));
		} else {
			super.handleKeyboardInput();
		}
	}

	@Override
	public void setCompletions(String... newCompletions) {
		String input = inputField.getText().substring(0, inputField.getCursorPosition());
		BlockPos pos = tabCompleter.getTargetBlockPos();
		List<String> list = new ArrayList<>();
		Collections.addAll(list, newCompletions);

		MinecraftForge.EVENT_BUS.post(new TabCompletionEvent(LogicalSide.CLIENT, Minecraft.getInstance().player, input.split(" ")[0], pos, pos != null, list));
		super.setCompletions(list.toArray(new String[0]));
	}

}
