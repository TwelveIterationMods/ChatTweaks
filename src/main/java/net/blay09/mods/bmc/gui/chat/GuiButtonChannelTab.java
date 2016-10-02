package net.blay09.mods.bmc.gui.chat;

import net.blay09.mods.bmc.ChatTweaks;
import net.blay09.mods.bmc.chat.ChatChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonChannelTab extends GuiButton {

	private final ChatChannel channel;

	public GuiButtonChannelTab(int buttonId, int x, int y, FontRenderer fontRenderer, ChatChannel channel) {
		super(buttonId, x, y, fontRenderer.getStringWidth("[" + channel.getName() + "]"), 10, "[" + channel.getName() + "]");
		this.channel = channel;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRendererObj;
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			boolean isActiveChannel = (channel == ChatTweaks.getChatHandler().getActiveChannel());
			boolean hasNewMessages = channel.hasUnreadMessages();
			int hoverState = getHoverState(hovered);
			int textColor = isActiveChannel ? 0xFFFFFFFF : 0xFF999999;
			if(hasNewMessages && !channel.isMuted()) {
				textColor = 0xFFFF0000;
			} else if(hoverState == 2 && !isActiveChannel) {
				textColor = 0xFFCCCCCC;
			}
			drawCenteredString(fondRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
		}
	}

	public ChatChannel getChannel() {
		return channel;
	}
}
