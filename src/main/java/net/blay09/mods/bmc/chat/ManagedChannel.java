package net.blay09.mods.bmc.chat;

import net.blay09.mods.bmc.api.IChatChannel;
import net.blay09.mods.bmc.api.IManagedChannel;

public class ManagedChannel extends ChatChannel implements IManagedChannel {

	private String id;
	private ChatChannel overrideSettings;

	public ManagedChannel(String id, String name) {
		super(name);
		this.id = id;
	}

	@Override
	public IChatChannel getOverrideSettings() {
		return overrideSettings;
	}

	@Override
	public boolean isManaged() {
		return true;
	}

}
