package net.blay09.mods.chattweaks.gui.chat;

import com.google.common.base.Strings;
import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.ChatTweaksConfig;
import net.blay09.mods.chattweaks.ChatViewManager;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.blay09.mods.chattweaks.chat.MessageStyle;
import net.blay09.mods.chattweaks.chat.emotes.EmoteRegistry;
import net.blay09.mods.chattweaks.event.ClientChatEvent;
import net.blay09.mods.chattweaks.event.TabCompletionEvent;
import net.blay09.mods.chattweaks.event.ChatComponentClickEvent;
import net.blay09.mods.chattweaks.event.ChatComponentHoverEvent;
import net.blay09.mods.chattweaks.gui.config.GuiChatView;
import net.blay09.mods.chattweaks.gui.config.GuiChatViewsConfig;
import net.blay09.mods.chattweaks.gui.config.GuiFactory;
import net.blay09.mods.chattweaks.gui.emotes.GuiButtonEmotes;
import net.blay09.mods.chattweaks.gui.emotes.GuiOverlayEmotes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.command.CommandBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class GuiChatExt extends GuiChat {

	private GuiOverlayEmotes emoteMenu;

	public GuiChatExt(String defaultText) {
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

		buttonList.add(new GuiButtonSettings(0, width - 16, height - 14));
		buttonList.add(new GuiButtonEmotes(0, width - 30, height - 14));

		if (emoteMenu != null) {
			emoteMenu.initGui();
		}

		updateChannelButtons();
	}

	public void updateChannelButtons() {
		buttonList.removeIf(p -> p instanceof GuiButtonChatView);
		if(ChatViewManager.getViews().size() > 1) {
			int x = 2;
			int y = height - 25;
			for (ChatView chatView : ChatViewManager.getViews()) {
				if (chatView.getMessageStyle() != MessageStyle.Chat) {
					continue;
				}
				GuiButtonChatView btnChatView = new GuiButtonChatView(-1, x, y, Minecraft.getMinecraft().fontRenderer, chatView);
				buttonList.add(btnChatView);
				x += btnChatView.width + 2;
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		if (button instanceof GuiButtonSettings) {
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
				mc.displayGuiScreen(new GuiChatView(null, ChatViewManager.getActiveView()));
			} else {
				mc.displayGuiScreen(new GuiFactory.ConfigGUI(this));
			}
		} else if(button instanceof GuiButtonChatView) {
			ChatViewManager.setActiveView(((GuiButtonChatView) button).getView());
		} else if (button instanceof GuiButtonEmotes) {
			if (emoteMenu == null) {
				emoteMenu = new GuiOverlayEmotes(this);
				emoteMenu.initGui();
			} else {
				emoteMenu.close();
				emoteMenu = null;
			}
		} else if (emoteMenu != null) {
			emoteMenu.actionPerformed(button);
		}
	}

	@Override
	public void sendChatMessage(String message, boolean addToSentMessages) {
		ClientChatEvent event = new ClientChatEvent(message);
		if (ChatViewManager.getActiveView().getOutgoingPrefix() != null && !event.getMessage().startsWith("/")) {
			event.setMessage(ChatViewManager.getActiveView().getOutgoingPrefix() + event.getMessage());
		}
		String newMessage;
		if (MinecraftForge.EVENT_BUS.post(event)) {
			newMessage = null;
		} else {
			newMessage = event.getMessage();
		}
		if (!Strings.isNullOrEmpty(newMessage)) {
			if (addToSentMessages) {
				// Store the originally typed message, not the potentially prefixed one.
				mc.ingameGUI.getChatGUI().addToSentMessages(message);
			}
			super.sendChatMessage(newMessage, false);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		if(emoteMenu != null && emoteMenu.isMouseInside()) {
			int delta = Mouse.getEventDWheel();
			if (delta != 0) {
				delta = MathHelper.clamp(delta, -1, 1);
				if (!isShiftKeyDown()) {
					delta *= 7;
				}
				emoteMenu.mouseScrolled(delta);
				return;
			}
		}
		super.handleMouseInput();
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		if(Keyboard.getEventKeyState() && ChatTweaks.keySwitchChatView.isActiveAndMatches(Keyboard.getEventKey())) {
			ChatViewManager.setActiveView(ChatViewManager.getNextChatView(ChatViewManager.getActiveView()));
		} else {
			super.handleKeyboardInput();
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (emoteMenu != null) {
			emoteMenu.drawOverlay(mouseX, mouseY);
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void setCompletions(String... newCompletions) {
		String input = inputField.getText().substring(0, inputField.getCursorPosition());
		BlockPos pos = tabCompleter.getTargetBlockPos();
		List<String> list = new ArrayList<>();
		Collections.addAll(list, newCompletions);

		MinecraftForge.EVENT_BUS.post(new TabCompletionEvent(Side.CLIENT, Minecraft.getMinecraft().player, input.split(" ")[0], pos, pos != null, list));
		super.setCompletions(list.toArray(new String[list.size()]));
	}

	@Override
	protected void handleComponentHover(ITextComponent component, int x, int y) {
		if (!MinecraftForge.EVENT_BUS.post(new ChatComponentHoverEvent(component, x, y))) {
			super.handleComponentHover(component, x, y);
		}
	}

	@Override
	protected boolean handleComponentClick(ITextComponent component) {
		if(component != null) {
			if (MinecraftForge.EVENT_BUS.post(new ChatComponentClickEvent(component))) {
				return true;
			}
			ClickEvent clickEvent = component.getStyle().getClickEvent();
			if (clickEvent != null) {
				if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
					String url = clickEvent.getValue();
					String directURL = null;
					for (Function<String, String> function : ChatTweaks.getImageURLTransformers()) {
						directURL = function.apply(url);
						if (directURL != null) {
							break;
						}
					}
					if (directURL != null) {
						try {
							Minecraft.getMinecraft().displayGuiScreen(new GuiImagePreview(Minecraft.getMinecraft().currentScreen, new URL(url), new URL(directURL)));
							return true;
						} catch (MalformedURLException e) {
							ChatTweaks.logger.error("Could not open image preview: ", e);
						}
					}
				}
			}
		}
		return super.handleComponentClick(component);
	}
}
