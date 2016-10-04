package net.blay09.mods.bmc.chat.emotes;

import java.util.Collection;

public interface IEmoteGroup {
	void addEmote(IEmote emote);

	String getName();

	Collection<IEmote> getEmotes();
}
