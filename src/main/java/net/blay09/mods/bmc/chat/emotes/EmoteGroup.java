package net.blay09.mods.bmc.chat.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteGroup;

import java.util.Collection;
import java.util.List;

public class EmoteGroup implements IEmoteGroup {

	private final String name;
	private final List<IEmote> emotes = Lists.newArrayList();

	public EmoteGroup(String name) {
		this.name = name;
	}

	@Override
	public void addEmote(IEmote emote) {
		emotes.add(emote);
	}

	public Collection<IEmote> getEmotes() {
		return emotes;
	}

	@Override
	public String getName() {
		return name;
	}

}
