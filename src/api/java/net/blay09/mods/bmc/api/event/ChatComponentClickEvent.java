package net.blay09.mods.bmc.api.event;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatComponentClickEvent extends Event {

	private final ITextComponent component;

	public ChatComponentClickEvent(ITextComponent component) {
		this.component = component;
	}

	public ITextComponent getComponent() {
		return component;
	}

}
