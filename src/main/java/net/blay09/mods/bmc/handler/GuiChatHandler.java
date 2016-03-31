package net.blay09.mods.bmc.handler;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.event.ChatComponentClickEvent;
import net.blay09.mods.bmc.api.event.TabCompletionEvent;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.chat.ChatMacros;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.blay09.mods.bmc.gui.chat.*;
import net.blay09.mods.bmc.gui.emotes.GuiButtonEmote;
import net.blay09.mods.bmc.gui.emotes.GuiButtonEmoteGroup;
import net.blay09.mods.bmc.gui.emotes.GuiButtonEmotes;
import net.blay09.mods.bmc.gui.emotes.GuiOverlayEmotes;
import net.blay09.mods.bmc.gui.settings.GuiOverlaySettings;
import net.blay09.mods.bmc.gui.settings.GuiButtonSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.command.CommandBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

@SuppressWarnings("unused")
public class GuiChatHandler {

	private GuiOverlayEmotes overlayEmotes;
	private GuiOverlaySettings overlaySettings;

	@SubscribeEvent
	public void onOpenGui(GuiOpenEvent event) {
		if(event.getGui() instanceof GuiSleepMP) {
			event.setGui(new GuiSleepMPExt());
			overlaySettings = null;
		} else if (event.getGui() instanceof GuiChat) {
			event.setGui(new GuiChatExt(((GuiChat) event.getGui()).defaultInputFieldText));
			overlaySettings = null;
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

			if(overlayEmotes != null) {
				overlayEmotes = new GuiOverlayEmotes(event.getGui());
			}
			if(overlaySettings != null) {
				ChatChannel selectedChannel = overlaySettings.getSelectedChannel();
				overlaySettings = new GuiOverlaySettings(event.getGui());
				overlaySettings.selectChannel(selectedChannel);
			}
		}
	}

	@SubscribeEvent
	public void onActionPerformedGuiChat(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (event.getGui() instanceof GuiChat) {
			if(event.getButton() instanceof GuiButtonChannelTab) {
				BetterMinecraftChat.getChatHandler().setActiveChannel(((GuiButtonChannelTab) event.getButton()).getChannel());
				if(overlaySettings != null) {
					overlaySettings.selectChannel(((GuiButtonChannelTab) event.getButton()).getChannel());
				}
			} else if(event.getButton() instanceof GuiButtonEmotes) {
				if (overlayEmotes != null) {
					overlayEmotes.clear(true);
					overlayEmotes = null;
				} else {
					overlayEmotes = new GuiOverlayEmotes(event.getGui());
				}
			} else if(event.getButton() instanceof GuiButtonEmoteGroup) {
				if (overlayEmotes != null) {
					overlayEmotes.displayGroup(((GuiButtonEmoteGroup) event.getButton()).getEmoteGroup());
				}
			} else if(event.getButton() instanceof GuiButtonSettings) {
				if(overlaySettings != null) {
					overlaySettings.apply(true);
					overlaySettings.clear();
					overlaySettings = null;
				} else {
					overlaySettings = new GuiOverlaySettings(event.getGui());
				}
			} else if(event.getButton() instanceof GuiButtonEmote) {
				((GuiChat) event.getGui()).inputField.writeText(" " + ((GuiButtonEmote) event.getButton()).getEmote().getCode() + " ");
			} else if(overlaySettings != null) {
				overlaySettings.actionPerformed(event.getButton());
			}
		}
	}

	@SubscribeEvent
	public void onDrawGuiChatPre(GuiScreenEvent.DrawScreenEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			if(overlayEmotes != null) {
				overlayEmotes.drawOverlay();
			}
			if(overlaySettings != null) {
				overlaySettings.drawOverlayBackground(event.getMouseX(), event.getMouseY());
			}
		}
	}

	@SubscribeEvent
	public void onDrawGuiChatPre(GuiScreenEvent.DrawScreenEvent.Post event) {
		if(event.getGui() instanceof GuiChat) {
			if(overlaySettings != null) {
				overlaySettings.drawOverlay(event.getMouseX(), event.getMouseY());
			}
		}
	}

	@SubscribeEvent
	public void onGuiKeyInput(GuiScreenEvent.KeyboardInputEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			if (Keyboard.getEventKeyState()) {
				int keyCode = Keyboard.getEventKey();
				if (overlaySettings == null && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) && keyCode == Keyboard.KEY_TAB) {
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
							ITextComponent result = new TextComponentString("Text stored in Macro #" + (index + 1) + ".");
							result.getChatStyle().setColor(TextFormatting.AQUA);
							Minecraft.getMinecraft().thePlayer.addChatComponentMessage(result);
							inputField.setText("");
						}
					} else if(!Strings.isNullOrEmpty(ChatMacros.getChatMacro(index))) {
						inputField.setText(ChatMacros.getChatMacro(index));
					}
				} else if(overlaySettings != null) {
					if(overlaySettings.keyTyped(Keyboard.getEventKey(), Keyboard.getEventCharacter())) {
						event.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onGuiMouseInput(GuiScreenEvent.MouseInputEvent.Pre event) {
		if(event.getGui() instanceof GuiChat) {
			if(Mouse.getEventButtonState() && overlaySettings != null) {
				ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
				if(overlaySettings.mouseClicked(Mouse.getEventX() / resolution.getScaleFactor(), resolution.getScaledHeight() - Mouse.getEventY() / resolution.getScaleFactor(), Mouse.getEventButton())) {
					event.setCanceled(true);
					return;
				}
			}
			if(overlayEmotes != null) {
				int delta = Mouse.getEventDWheel();
				if (delta != 0) {
					overlayEmotes.scroll(delta > 0);
				}
			}
		}
	}

	@SubscribeEvent
	public void onClickChatComponent(ChatComponentClickEvent event) {
		ClickEvent clickEvent = event.getComponent().getChatStyle().getChatClickEvent();
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

}
