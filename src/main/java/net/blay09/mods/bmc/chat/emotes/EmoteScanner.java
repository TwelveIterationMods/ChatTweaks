package net.blay09.mods.bmc.chat.emotes;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.blay09.mods.bmc.chat.ChatMessage;
import net.blay09.mods.bmc.image.ChatImageEmote;

import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Matcher;

public class EmoteScanner implements IEmoteScanner {

	@Override
	public List<PositionedEmote> scanForEmotes(String message, @Nullable Predicate<IEmote> emoteFilter) {
		List<PositionedEmote> emotes = Lists.newArrayList();
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
				emotes.add(new PositionedEmote(emote, matcher.start(), matcher.end()));
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
				emotes.add(new PositionedEmote(emote, lastIdx, spaceIdx - 1));
			}
			lastIdx = spaceIdx + 1;
		}
		return emotes;
	}

	public String scanEmotes(String text, ChatMessage message) {
		int index = 0;
		StringBuilder sb = new StringBuilder();
		List<PositionedEmote> emotes = scanForEmotes(text, null);
		for(PositionedEmote emoteData : emotes) {
			if (index < emoteData.getStart()) {
				sb.append(text.substring(index, emoteData.getStart()));
			}
			int imageIndex = sb.length() + 1;
			sb.append("\u00a7*");
			for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
				sb.append(' ');
			}
			message.addImage(new ChatImageEmote(imageIndex, emoteData.getEmote()));
			index = emoteData.getEnd() + 1;
		}
		if(index < text.length()) {
			sb.append(text.substring(index));
		}
		text = sb.toString();
		return text;
	}
}
