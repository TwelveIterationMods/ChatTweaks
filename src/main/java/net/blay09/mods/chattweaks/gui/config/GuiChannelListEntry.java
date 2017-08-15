package net.blay09.mods.chattweaks.gui.config;

import net.blay09.mods.chattweaks.chat.ChatChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiChannelListEntry implements GuiListExtended.IGuiListEntry {

	private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");

	private final Minecraft mc;
	private final GuiChatViewChannels parentGui;
	private final ChatChannel channel;

	public GuiChannelListEntry(Minecraft mc, GuiChatViewChannels parentGui, ChatChannel channel) {
		this.mc = mc;
		this.parentGui = parentGui;
		this.channel = channel;
	}

	public String getName() {
		return channel.getName();
	}

	@Override
	public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
		mc.getTextureManager().bindTexture(channel.getIcon());
		GlStateManager.color(1f, 1f, 1f, 1f);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, 32, 32, 32f, 32f);
		mc.fontRenderer.drawStringWithShadow(channel.getName(), x + 34, y + 1, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow(channel.getDescription(), x + 34, y + 12, 0xDDDDDD);

		if (mc.gameSettings.touchscreen || isSelected) {
			mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
			Gui.drawRect(x, y, x + 32, y + 32, 0x44FFFFFF);
			GlStateManager.color(1f, 1f, 1f, 1f);
			int relMouseX = mouseX - x;
			if (canMoveRight()) {
				if (relMouseX < 32) {
					Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 32f, 32, 32, 256f, 256f);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(x, y, 0f, 0f, 32, 32, 256f, 256f);
				}
			} else if (canMoveLeft()) {
				if (relMouseX < 16) {
					Gui.drawModalRectWithCustomSizedTexture(x, y, 32f, 32f, 32, 32, 256f, 256f);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(x, y, 32f, 0f, 32, 32, 256f, 256f);
				}
			}
		}
	}

	private boolean canMoveLeft() {
		return parentGui.isSelected(channel);
	}

	private boolean canMoveRight() {
		return !parentGui.isSelected(channel);
	}

	@Override
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		if (relativeX <= 32) {
			if (canMoveRight()) {
				parentGui.selectChannel(this);
				return true;
			}

			if (relativeX < 16 && canMoveLeft()) {
				parentGui.unselectChannel(this);
				return true;
			}
		}

		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
	}

}
