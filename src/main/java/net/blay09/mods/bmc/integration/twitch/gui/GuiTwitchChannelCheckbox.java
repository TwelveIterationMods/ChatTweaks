package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.integration.twitch.handler.TwitchChannel;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiTwitchChannelCheckbox extends GuiCheckBox {

	private static final int BOX_WIDTH = 11;

	private final TwitchChannel channel;
	private final GuiTwitchChannels parentScreen;

	public GuiTwitchChannelCheckbox(int id, int xPos, int yPos, GuiTwitchChannels parentScreen, TwitchChannel channel) {
		super(id, xPos, yPos, channel.getName(), channel.isActive());
		this.channel = channel;
		this.parentScreen = parentScreen;
	}

	public TwitchChannel getChannel() {
		return channel;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
		if(parentScreen.getSelectedChannel() == channel) {
			packedFGColour = 0xFFFFFF;
		} else {
			packedFGColour = hovered ? 0xEEEEEE : 0x777777;
		}
		super.drawButton(mc, mouseX, mouseY);
		setIsChecked(channel.isActive());
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		if (enabled && visible && mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height) {
			if(mouseX < xPosition + BOX_WIDTH) {
				// Only toggle the checkbox if it was clicked itself, ignore clicks on the label
				setIsChecked(!isChecked());
			}
			return true;
		}
		return false;
	}
}
