package net.blay09.mods.bmc.handler;

import net.blay09.mods.bmc.BetterMinecraftChat;
import net.blay09.mods.bmc.BetterMinecraftChatConfig;
import net.blay09.mods.bmc.api.chat.MessageStyle;
import net.blay09.mods.bmc.api.image.IChatImage;
import net.blay09.mods.bmc.balyware.gui.GuiUtils;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.blay09.mods.bmc.api.event.DrawChatMessageEvent;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.blay09.mods.bmc.coremod.RGBFontRenderer;
import net.blay09.mods.bmc.chat.emotes.EmoteRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SuppressWarnings("unused")
public class RenderHandler {

	private IChatImage hoverImage;
	private int lastChatLine;
	private boolean backgroundColorAlternate;

	@SubscribeEvent
	public void onDrawChatMessagePre(DrawChatMessageEvent.Pre event) {
		if(lastChatLine != event.getChatLine().getChatLineID()) {
			backgroundColorAlternate = !backgroundColorAlternate;
		}
		lastChatLine = event.getChatLine().getChatLineID();
		int chatWidth = (int) (Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatWidth() / Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatScale());
		ChatMessage chatLine = (ChatMessage) BetterMinecraftChat.getChatHandler().getChatLine(event.getChatLine().getChatLineID());
		if (chatLine != null) {
			if(chatLine.hasBackgroundColor()) {
				Gui.drawRect(event.getX(), event.getY() - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, event.getX() + chatWidth + 4, event.getY(), chatLine.getBackgroundColor() | ((event.getAlpha() / 2) << 24));
			} else {
				Gui.drawRect(event.getX(), event.getY() - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, event.getX() + chatWidth + 4, event.getY(), (backgroundColorAlternate ? BetterMinecraftChatConfig.backgroundColor1 : BetterMinecraftChatConfig.backgroundColor2) | ((event.getAlpha() / 2) << 24));
			}
			if(chatLine.hasRGBColors()) {
				RGBFontRenderer.setBuffer(chatLine.getRGBBuffer());
			}
		}
	}

	@SubscribeEvent
	public void onDrawChatMessagePost(DrawChatMessageEvent.Post event) {
		List<ChatLine> wrappedChatLines = Minecraft.getMinecraft().ingameGUI.getChatGUI().drawnChatLines;
		int thisIndex = wrappedChatLines.indexOf(event.getChatLine());
		int thisOffset = 0;
		int wrappedLinesBefore = 0;
		for(int i = thisIndex + 1; i < wrappedChatLines.size(); i++) {
			if(wrappedChatLines.get(i) == null || event.getChatLine() == null) {
				continue; // wth, this shouldn't even be able to be null but whatever
			}
			if(wrappedChatLines.get(i).getChatLineID() != event.getChatLine().getChatLineID()) {
				break;
			}
			String formattedText = wrappedChatLines.get(i).getChatComponent().getFormattedText();
			thisOffset += formattedText.length() - 2;
			wrappedLinesBefore++;
		}
		float chatScale = Minecraft.getMinecraft().ingameGUI.getChatGUI().getChatScale();
		ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		int mouseX = Mouse.getX() / scaledResolution.getScaleFactor();
		int mouseY = Mouse.getY() / scaledResolution.getScaleFactor();
		int invMouseY = scaledResolution.getScaledHeight() - mouseY;
		mouseX = MathHelper.floor_float((float) mouseX / chatScale);
		invMouseY = MathHelper.floor_float((float) invMouseY / chatScale);
		ChatMessage chatLine = (ChatMessage) BetterMinecraftChat.getChatHandler().getChatLine(event.getChatLine().getChatLineID());
		if (chatLine != null && chatLine.hasImages()) {
			String formattedText = event.getChatLine().getChatComponent().getFormattedText();
			for(IChatImage image : chatLine.getImages()) {
				if(image.getIndex() >= thisOffset && image.getIndex() < thisOffset + formattedText.length()) {
					int offset = Math.min(formattedText.length() - 1, image.getIndex() - thisOffset + (wrappedLinesBefore * 2) + 1);
					String beforeText = formattedText.substring(0, offset);
					int renderOffset = Minecraft.getMinecraft().fontRendererObj.getStringWidth(beforeText);
					int spaceWidth = Minecraft.getMinecraft().fontRendererObj.getCharWidth(' ') * image.getSpaces();
					GlStateManager.pushMatrix();
					float scale = image.getScale();
//					scale = 1f;
					GlStateManager.scale(scale, scale, 1f);
					int renderWidth = (int) (image.getWidth() * scale);
					int renderHeight = (int) (image.getHeight() * scale);
					int renderX = event.getX() + renderOffset + spaceWidth / 2 - renderWidth / 2;
					int renderY = event.getY() - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2 - renderHeight / 2;
					image.draw((int) (renderX / scale), (int) (renderY / scale), event.getAlpha());
					GlStateManager.popMatrix();
					float offsetX = 2;
					float offsetY = (scaledResolution.getScaledHeight() - 48) + 20f;
					if(mouseX >= (offsetX + renderX) && mouseX < offsetX + renderX + renderWidth && invMouseY >= offsetY + renderY - renderHeight && invMouseY < offsetY + renderY) {
						hoverImage = image;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderOverlayChat(RenderGameOverlayEvent.Post event) {
		if(event.getType() != RenderGameOverlayEvent.ElementType.CHAT) {
			return;
		}
		EmoteRegistry.runDisposal();
		backgroundColorAlternate = false;
		if(hoverImage != null && Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
			GuiUtils.drawTooltip(hoverImage.getTooltip(), Mouse.getX() / event.getResolution().getScaleFactor(), event.getResolution().getScaledHeight() - Mouse.getY() / event.getResolution().getScaleFactor());
			hoverImage = null;
		}
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = mc.currentScreen;
		boolean isChatOpen = (gui instanceof GuiChat);
		if(!isChatOpen) {
			ScaledResolution resolution = new ScaledResolution(mc);
			int mouseX = Mouse.getEventX() / resolution.getScaleFactor();
			int invMouseY = resolution.getScaledHeight() - Mouse.getEventY() / resolution.getScaleFactor();
			int x = 2;
			int y = event.getResolution().getScaledHeight() - 25 + 1;
			for (ChatChannel channel : BetterMinecraftChat.getChatHandler().getChannels()) {
				if (channel.isHidden()) {
					continue;
				}
				boolean isActiveChannel = (channel == BetterMinecraftChat.getChatHandler().getActiveChannel());
				boolean hasNewMessages = channel.hasUnreadMessages();
				int tabHeight = 10;
				int tabWidth = mc.fontRendererObj.getStringWidth("[" + channel.getName() + "]");
				int textColor = isActiveChannel ? 0xFFFFFFFF : 0xFF999999;
				if (hasNewMessages) {
					textColor = 0xFFFF0000;
				}
				if (hasNewMessages && !channel.isMuted() && channel.getMessageStyle() == MessageStyle.Chat) {
					mc.fontRendererObj.drawStringWithShadow("[" + channel.getName() + "]", x, y, textColor);
				}
				x += tabWidth + 2;
			}
		}
	}

}
