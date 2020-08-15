package net.blay09.mods.chattweaks.chat.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.blay09.mods.chattweaks.api.ChatView;
import net.blay09.mods.chattweaks.core.ChatViewManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;

public class ChatViewButton extends Button {

	private final ChatView view;

	public ChatViewButton(int x, int y, FontRenderer fontRenderer, ChatView view) {
		super(x, y, fontRenderer.getStringWidth(getViewButtonText(view)), 10, new StringTextComponent(getViewButtonText(view)), button -> {
			ChatViewManager.setActiveView(view);
		});
		this.view = view;
	}

	private static String getViewButtonText(ChatView view) {
		return "[" + view.getName() + "]";
	}

	@Override
	public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		if (visible) {
			FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
			boolean isActiveChannel = (view == ChatViewManager.getActiveView());
			boolean hasNewMessages = view.hasUnreadMessages();
			int textColor = isActiveChannel ? 0xFFFFFFFF : 0xFF999999;
			if(hasNewMessages && !view.isMuted()) {
				textColor = 0xFFFF0000;
			} else if(isHovered && !isActiveChannel) {
				textColor = 0xFFCCCCCC;
			}
			drawCenteredString(matrixStack, fontRenderer, getMessage(), x + width / 2, y + (height - 8) / 2, textColor);
		}
	}

	public ChatView getView() {
		return view;
	}
}
