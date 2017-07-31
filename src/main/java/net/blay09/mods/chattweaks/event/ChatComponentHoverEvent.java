package net.blay09.mods.chattweaks.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatComponentHoverEvent extends Event {

	private final ITextComponent component;
	private final int x;
	private final int y;

	public ChatComponentHoverEvent(ITextComponent component, int x, int y) {
		this.component = component;
		this.x = x;
		this.y = y;
	}

	public ITextComponent getComponent() {
		return component;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
