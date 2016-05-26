package net.blay09.mods.bmc.integration.twitch.handler;

import net.blay09.mods.bmc.api.BetterMinecraftChatAPI;
import net.blay09.mods.bmc.api.chat.IChatChannel;

import javax.annotation.Nullable;

public class TwitchChannel {

	public enum DeletedMessages {
		SHOW,
		STRIKETHROUGH,
		REPLACE,
		HIDE;

		public static DeletedMessages fromName(String name) {
			try {
				return valueOf(name.toUpperCase());
			} catch (IllegalArgumentException e) {
				return HIDE;
			}
		}
	}

	private final String name;
	private String targetChannelName;
	private IChatChannel targetChannel;
	private boolean subscribersOnly;
	private DeletedMessages deletedMessages = DeletedMessages.SHOW;
	private boolean active;

	public TwitchChannel(String name) {
		this.name = name;
		targetChannelName = name;
	}

	public String getName() {
		return name;
	}

	public boolean isSubscribersOnly() {
		return subscribersOnly;
	}

	public void setSubscribersOnly(boolean subscribersOnly) {
		this.subscribersOnly = subscribersOnly;
	}

	public DeletedMessages getDeletedMessages() {
		return deletedMessages;
	}

	public void setDeletedMessages(DeletedMessages deletedMessages) {
		this.deletedMessages = deletedMessages;
	}

	public void setTargetTabName(String targetChannelName) {
		this.targetChannelName = targetChannelName;
		this.targetChannel = BetterMinecraftChatAPI.getChatChannel(targetChannelName, false);
	}

	public String getTargetTabName() {
		return targetChannelName;
	}

	@Nullable
	public IChatChannel getTargetTab() {
		return targetChannel;
	}

	public void setTargetTab(@Nullable IChatChannel targetChannel) {
		this.targetChannel = targetChannel;
		this.targetChannelName = targetChannel != null ? targetChannel.getName() : name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
