package net.blay09.mods.bmc.integration.twitch;

public class TwitchChannel {

	public enum DeletedMessages {
		SHOW,
		STRIKETHROUGH,
		REPLACE,
		HIDE;

		public static DeletedMessages fromName(String name) {
			return null;
		}
	}

	private final String name;
	private boolean subscribersOnly;
	private DeletedMessages deletedMessages;

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
}
