package net.blay09.mods.bmc.chat.emotes;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.emote.IEmote;
import net.blay09.mods.bmc.balyware.textcomponent.TextComponentTransformer;
import net.blay09.mods.bmc.image.ChatImage;
import net.blay09.mods.bmc.image.ChatImageEmote;
import net.minecraft.util.text.ITextComponent;

import java.util.List;
import java.util.regex.Matcher;

public class EmoteScanner extends TextComponentTransformer {

	private final List<ChatImage> images = Lists.newArrayList();
	private int offset;

	@Override
	public void begin(ITextComponent chatComponent) {
		offset = 0;
		images.clear();
	}

	@Override
	public ITextComponent transformStyle(ITextComponent component) {
		offset += component.getFormattedText().length();
		return component;
	}

	@Override
	public String transformText(ITextComponent component, String text) {
		if(text == null || text.trim().length() <= 1) {
			return text;
		}
		Matcher matcher = null;
		for(IEmote emote : EmoteRegistry.getRegexEmotes()) {
			if(matcher == null) {
				matcher = emote.getPattern().matcher(text);
			} else {
				matcher.usePattern(emote.getPattern());
			}
			while(matcher.find()) {
				int emoteOffset = offset + matcher.start();
				images.add(new ChatImageEmote(emoteOffset, emote));
				text = (matcher.start() > 0 ? text.substring(0, matcher.start()) : "") + Strings.repeat(" ", emote.getWidthInSpaces()) + (matcher.end() < text.length() ? text.substring(matcher.end()) : "");
				matcher.reset(text);
			}
		}
		StringBuilder sb = new StringBuilder();
		String[] words = text.split("(?<=\\s+)|(?=\\s+)");
		for (String word : words) {
			if (word.trim().isEmpty()) {
				sb.append(word);
				continue;
			}
			IEmote emote = EmoteRegistry.fromName(word);
			if (emote != null) {
				int emoteOffset = offset + sb.length() + 1;
				images.add(new ChatImageEmote(emoteOffset, emote));
				for (int j = 0; j < emote.getWidthInSpaces(); j++) {
					sb.append(' ');
				}
			} else {
				sb.append(word);
			}
		}
		return sb.toString();
	}

	public List<ChatImage> getImages() {
		return images;
	}

}
