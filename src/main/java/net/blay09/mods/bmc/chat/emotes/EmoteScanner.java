package net.blay09.mods.bmc.chat.emotes;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.api.emote.IEmoteScanner;
import net.blay09.mods.bmc.api.emote.PositionedEmote;

import java.util.List;
import java.util.regex.Matcher;

public class EmoteScanner implements IEmoteScanner {

	private final List<PositionedEmote> tmpEmotes = Lists.newArrayList();
	public List<PositionedEmote> scanForEmotes(String message, Predicate<IEmote> emoteFilter) {
		tmpEmotes.clear();
		Matcher matcher = null;
		for(IEmote emote : EmoteRegistry.getRegexEmotes()) {
			if(emoteFilter != null && !emoteFilter.apply(emote)) {
				break;
			}
			if(matcher == null) {
				matcher = emote.getPattern().matcher(message);
			} else {
				matcher.usePattern(emote.getPattern());
			}
			while(matcher.find()) {
				tmpEmotes.add(new PositionedEmote(emote, matcher.start(), matcher.end()));
			}
		}
		int lastIdx = 0;
		int spaceIdx = 0;
		while(spaceIdx < message.length()) {
			spaceIdx = message.indexOf(' ', lastIdx);
			if(spaceIdx == -1) {
				spaceIdx = message.length();
			}
			String word = message.substring(lastIdx, spaceIdx);
			IEmote emote = EmoteRegistry.fromName(word);
			if (emote != null && (emoteFilter == null || emoteFilter.apply(emote))) {
				tmpEmotes.add(new PositionedEmote(emote, lastIdx, spaceIdx - 1));
			}
			lastIdx = spaceIdx + 1;
		}
		return tmpEmotes;
	}

}
