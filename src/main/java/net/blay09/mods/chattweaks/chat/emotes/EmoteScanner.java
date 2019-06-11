package net.blay09.mods.chattweaks.chat.emotes;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class EmoteScanner implements IEmoteScanner {

	@Override
	public List<PositionedEmote> scanForEmotes(String message, @Nullable Predicate<IEmote> emoteFilter) {
		List<PositionedEmote> emotes = Lists.newArrayList();
		int lastIdx = 0;
		int spaceIdx = 0;
		while(spaceIdx < message.length()) {
			spaceIdx = message.indexOf(' ', lastIdx);
			if(spaceIdx == -1) {
				spaceIdx = message.length();
			}
			String word = message.substring(lastIdx, spaceIdx);
			IEmote emote = EmoteRegistry.fromName(word);
			if (emote != null && (emoteFilter == null || emoteFilter.test(emote))) {
				emotes.add(new PositionedEmote(emote, lastIdx, spaceIdx - 1));
			}
			lastIdx = spaceIdx + 1;
		}
		return emotes;
	}

}
