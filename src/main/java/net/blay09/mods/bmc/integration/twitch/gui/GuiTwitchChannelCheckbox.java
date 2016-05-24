package net.blay09.mods.bmc.integration.twitch.gui;

import net.blay09.mods.bmc.integration.twitch.TwitchChannel;
import net.minecraftforge.fml.client.config.GuiCheckBox;

public class GuiTwitchChannelCheckbox extends GuiCheckBox {

	private final TwitchChannel channel;

	public GuiTwitchChannelCheckbox(int id, int xPos, int yPos, TwitchChannel channel) {
		super(id, xPos, yPos, channel.getName(), channel.isActive());
		this.channel = channel;
	}

	public TwitchChannel getChannel() {
		return channel;
	}

}
