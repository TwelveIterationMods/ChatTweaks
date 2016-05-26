package net.blay09.mods.bmc.api.emote;

import com.google.common.base.Predicate;

import java.util.List;

public interface IEmoteScanner {
	List<PositionedEmote> scanForEmotes(String message, Predicate<IEmote> emoteFilter);
}
