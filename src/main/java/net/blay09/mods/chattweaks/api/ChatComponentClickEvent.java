package net.blay09.mods.chattweaks.api;

import net.minecraft.util.text.Style;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class ChatComponentClickEvent extends Event {

	private final Style style;

	public ChatComponentClickEvent(Style style) {
		this.style = style;
	}

	public Style getStyle() {
		return style;
	}

}
