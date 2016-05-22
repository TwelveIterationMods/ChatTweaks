package net.blay09.mods.bmc.integration.twitch;

import net.blay09.mods.bmc.api.IChatChannel;

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
	private DeletedMessages deletedMessages;
	private boolean active;

	public TwitchChannel(String name) {
		this.name = name;
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

	public void setTargetChannelName(String targetChannelName) {
		this.targetChannelName = targetChannelName;
	}

	public String getTargetChannelName() {
		return targetChannelName;
	}

	public IChatChannel getTargetChannel() {
		return targetChannel;
	}

	public void setTargetChannel(IChatChannel targetChannel) {
		this.targetChannel = targetChannel;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
