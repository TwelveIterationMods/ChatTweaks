package net.blay09.mods.chattweaks.chat.emotes;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public interface IEmoteScanner {
	List<PositionedEmote> scanForEmotes(String message, @Nullable Predicate<IEmote> emoteFilter);
}
