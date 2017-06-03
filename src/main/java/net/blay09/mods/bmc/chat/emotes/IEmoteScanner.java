package net.blay09.mods.bmc.chat.emotes;

import java.util.List;
import java.util.function.Predicate;

public interface IEmoteScanner {
	List<PositionedEmote> scanForEmotes(String message, Predicate<IEmote> emoteFilter);
}
