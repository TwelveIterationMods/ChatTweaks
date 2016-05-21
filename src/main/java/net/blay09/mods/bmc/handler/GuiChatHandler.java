package net.blay09.mods.bmc.handler;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.IGuiOverlay;
import net.blay09.mods.bmc.api.event.ChatComponentClickEvent;
import net.blay09.mods.bmc.api.event.TabCompletionEvent;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.ChatMacros;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.gui.chat.*;
import net.blay09.mods.bmc.gui.emotes.GuiButtonEmotes;
import net.blay09.mods.bmc.gui.emotes.GuiOverlayEmotes;
import net.blay09.mods.bmc.gui.settings.GuiTabSettings;
import net.blay09.mods.bmc.gui.settings.GuiButtonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.command.CommandBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class GuiChatHandler {

	private final List<IGuiOverlay> overlayList = Lists.newArrayList();

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiSleepMP) {
			event.setGui(new GuiSleepMPExt());
			clearOverlays();
		} else if (event.getGui() instanceof GuiChat) {
			event.setGui(new GuiChatExt(((GuiChat) event.getGui()).defaultInputFieldText));
			clearOverlays();
		}
	}

	@SubscribeEvent
	public void onTabComplete(TabCompletionEvent event) {
		if (event.getSide() == Side.CLIENT) {
			if (BetterMinecraftChatConfig.emoteTabCompletion) {
				event.getCompletions().addAll(CommandBase.getListOfStringsMatchingLastWord(new String[]{event.getInput()}, EmoteRegistry.getEmoteCodes()));
			}
		}
	}

	@SubscribeEvent
	public void onInitGuiChat(GuiScreenEvent.InitGuiEvent.Post event) {
		if (event.getGui() instanceof GuiChat) {
			event.getButtonList().add(new GuiButtonSettings(-1, event.getGui().width - 16, event.getGui().height - 14));
			event.getButtonList().add(new GuiButtonEmotes(-1, event.getGui().width - 16 - 14, event.getGui().height - 14));

			updateChannelButtons(event.getGui());

			List<IGuiOverlay> oldList = Lists.newArrayList(overlayList);
			overlayList.clear();
			for(IGuiOverlay overlay : oldList) {
				IGuiOverlay newOverlay = overlay.recreateFor(overlay, event.getGui());
				newOverlay.initGui();
				overlayList.add(newOverlay);
			}
		}
	}

	@SubscribeEvent
	public void onActionPerformedGuiChat(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (event.getGui() instanceof GuiChat) {
			if(event.getButton() instanceof GuiButtonChannelTab) {
				BetterMinecraftChat.getChatHandler().setActiveChannel(((GuiButtonChannelTab) event.getButton()).getChannel());
			} else if(event.getButton() instanceof GuiButtonEmotes) {
				GuiOverlayEmotes overlayEmotes = getOverlay(GuiOverlayEmotes.class);
				if(overlayEmotes == null) {
					overlayList.add(new GuiOverlayEmotes(event.getGui()));
				} else {
					removeOverlay(overlayEmotes);
				}
			} else if(event.getButton() instanceof GuiButtonSettings) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiTabSettings(null));
			} else {
				for(IGuiOverlay overlay : overlayList) {
					overlay.actionPerformed(event.getButton());
				}
			}
		}
	}

	@SubscribeEvent
	public void onDrawGuiChatPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			for(IGuiOverlay overlay : overlayList) {
				overlay.drawOverlayBackground(event.getMouseX(), event.getMouseY());
			}
		}
	}

	@SubscribeEvent
	public void onDrawGuiChatPre(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(event.getGui() instanceof GuiChat) {
			for(IGuiOverlay overlay : overlayList) {
				overlay.drawOverlay(event.getMouseX(), event.getMouseY());
			}
		}
	}

	@SubscribeEvent
	public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			if (Keyboard.getEventKeyState()) {
				int keyCode = Keyboard.getEventKey();
				if ((Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && keyCode == Keyboard.KEY_TAB) {
					ChatChannel channel = BetterMinecraftChat.getChatHandler().getNextChatChannel(BetterMinecraftChat.getChatHandler().getActiveChannel());
					if(channel != null) {
						BetterMinecraftChat.getChatHandler().setActiveChannel(channel);
					}
					event.setCanceled(true);
				} else if (keyCode >= Keyboard.KEY_F5 && keyCode <= Keyboard.KEY_F8) {
					int index = keyCode - Keyboard.KEY_F5;
					GuiTextField inputField = ((GuiChat) event.getGui()).inputField;
					if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
						if(!inputField.getText().isEmpty()) {
							ChatMacros.setChatMacro(index, inputField.getText());
							ITextComponent result = new TextComponentTranslation(BetterMinecraftChat.MOD_ID + ":gui.chat.textStoredInMacro", index + 1);
							result.getStyle().setColor(TextFormatting.AQUA);
							Minecraft.getMinecraft().thePlayer.addChatComponentMessage(result);
							inputField.setText("");
						}
					} else if(!Strings.isNullOrEmpty(ChatMacros.getChatMacro(index))) {
						inputField.setText(ChatMacros.getChatMacro(index));
					}
				} else {
					for(IGuiOverlay overlay : overlayList) {
						if(overlay.keyTyped(Keyboard.getEventKey(), Keyboard.getEventCharacter())) {
							event.setCanceled(true);
							break;
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			if(Mouse.getEventButtonState()) {
				ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
				int mouseX = Mouse.getEventX() / resolution.getScaleFactor();
				int mouseY = resolution.getScaledHeight() - Mouse.getEventY() / resolution.getScaleFactor();
				int mouseButton = Mouse.getEventButton();
				for(IGuiOverlay overlay : overlayList) {
					if(overlay.mouseClicked(mouseX, mouseY, mouseButton)) {
						event.setCanceled(true);
						return;
					}
				}
			}
			int delta = Mouse.getEventDWheel();
			if (delta != 0) {
				for(IGuiOverlay overlay : overlayList) {
					overlay.mouseScrolled(delta);
				}
			}
		}
	}

	@SubscribeEvent
	public void onClickChatComponent(ChatComponentClickEvent event) {
		ClickEvent clickEvent = event.getComponent().getStyle().getClickEvent();
		if (clickEvent != null) {
			if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
				String url = clickEvent.getValue();
				String directURL = null;
				for(Function<String, String> function : BetterMinecraftChat.getImageURLTransformers()) {
					directURL = function.apply(url);
					if(directURL != null) {
						break;
					}
				}
				if (directURL != null) {
					try {
						Minecraft.getMinecraft().displayGuiScreen(new GuiImagePreview(Minecraft.getMinecraft().currentScreen, new URL(url), new URL(directURL)));
						event.setCanceled(true);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void updateChannelButtons(GuiScreen gui) {
		Iterator<GuiButton> it = gui.buttonList.iterator();
		while(it.hasNext()) {
			GuiButton button = it.next();
			if(button instanceof GuiButtonChannelTab) {
				it.remove();
			}
		}
		int x = 2;
		int y = gui.height - 25;
		for (ChatChannel channel : BetterMinecraftChat.getChatHandler().getChannels()) {
			if(channel.isHidden()) {
				continue;
			}
			GuiButtonChannelTab btnChannel = new GuiButtonChannelTab(-1, x, y, Minecraft.getMinecraft().fontRendererObj, channel);
			gui.buttonList.add(btnChannel);
			x += btnChannel.width + 2;
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends IGuiOverlay> T getOverlay(Class<T> clazz) {
		for(IGuiOverlay overlay : overlayList) {
			if(clazz.isAssignableFrom(overlay.getClass())) {
				return (T) overlay;
			}
		}
		return null;
	}

	public void addOverlay(IGuiOverlay overlay) {
		overlayList.add(overlay);
		overlay.initGui();
	}

	public void removeOverlay(IGuiOverlay overlay) {
		overlay.onGuiClosed();
		overlayList.remove(overlay);
	}

	public void clearOverlays() {
		for(IGuiOverlay overlay : overlayList) {
			overlay.onGuiClosed();
		}
		overlayList.clear();
	}

	public void switchOverlay(IGuiOverlay from, IGuiOverlay to) {
		removeOverlay(from);
		addOverlay(to);
	}
}
