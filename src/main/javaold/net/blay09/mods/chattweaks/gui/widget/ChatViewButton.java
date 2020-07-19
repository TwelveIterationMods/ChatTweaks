package net.blay09.mods.chattweaks.gui.widget;

import net.blay09.mods.chattweaks.ChatViewManager;
import net.blay09.mods.chattweaks.chat.ChatView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;

public class ChatViewButton extends Button {

	private final ChatView view;

	public ChatViewButton(int buttonId, int x, int y, FontRenderer fontRenderer, ChatView view) {
		super(buttonId, x, y, fontRenderer.getStringWidth("[" + view.getName() + "]"), 10, "[" + view.getName() + "]");
		this.view = view;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			FontRenderer fondRenderer = mc.fontRenderer;
			this.hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
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
			drawCenteredString(fondRenderer, displayString, x + width / 2, y + (height - 8) / 2, textColor);
		}
	}

	public ChatView getView() {
		return view;
	}
}
