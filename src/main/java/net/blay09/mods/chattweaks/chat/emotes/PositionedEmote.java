package net.blay09.mods.chattweaks.chat.emotes;

public class PositionedEmote {
	private final IEmote emote;
	private final int start;
	private final int end;

	public PositionedEmote(IEmote emote, int start, int end) {
		this.emote = emote;
		this.start = start;
		this.end = end;
	}

	public IEmote getEmote() {
		return emote;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}
}
