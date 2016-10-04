package net.blay09.mods.bmc.gui.chat;

import net.blay09.mods.bmc.ChatViewManager;
import net.blay09.mods.bmc.chat.ChatView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiButtonChatView extends GuiButton {

	private final ChatView view;

	public GuiButtonChatView(int buttonId, int x, int y, FontRenderer fontRenderer, ChatView view) {
		super(buttonId, x, y, fontRenderer.getStringWidth("[" + view.getName() + "]"), 10, "[" + view.getName() + "]");
		this.view = view;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRendererObj;
			this.hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
			mouseDragged(mc, mouseX, mouseY);
			boolean isActiveChannel = (view == ChatViewManager.getActiveView());
			boolean hasNewMessages = view.hasUnreadMessages();
			int hoverState = getHoverState(hovered);
			int textColor = isActiveChannel ? 0xFFFFFFFF : 0xFF999999;
			if(hasNewMessages && !view.isMuted()) {
				textColor = 0xFFFF0000;
			} else if(hoverState == 2 && !isActiveChannel) {
				textColor = 0xFFCCCCCC;
			}
			drawCenteredString(fondRenderer, displayString, xPosition + width / 2, yPosition + (height - 8) / 2, textColor);
		}
	}

	public ChatView getView() {
		return view;
	}
}
