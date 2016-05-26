package net.blay09.mods.bmc.chat.emotes;

import com.google.common.collect.Lists;
import net.blay09.mods.bmc.api.emote.PositionedEmote;
import net.blay09.mods.bmc.balyware.textcomponent.TextComponentTransformer;
import net.blay09.mods.bmc.image.ChatImage;
import net.blay09.mods.bmc.image.ChatImageEmote;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class EmoteTransformer extends TextComponentTransformer {

	private final EmoteScanner emoteScanner = new EmoteScanner();
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
		int index = 0;
		StringBuilder sb = new StringBuilder();
		for(PositionedEmote emoteData : emoteScanner.scanForEmotes(text, null)) {
			if (index < emoteData.getStart()) {
				sb.append(text.substring(index, emoteData.getStart()));
			}
			int imageIndex = sb.length() + offset + 1;
			for (int i = 0; i < emoteData.getEmote().getWidthInSpaces(); i++) {
				sb.append(' ');
			}
			images.add(new ChatImageEmote(imageIndex, emoteData.getEmote()));
			index = emoteData.getEnd() + 1;
		}
		if(index < text.length()) {
			sb.append(text.substring(index));
		}
		return sb.toString();
	}

	public List<ChatImage> getImages() {
		return images;
	}

}
