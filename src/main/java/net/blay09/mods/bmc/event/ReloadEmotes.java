package net.blay09.mods.bmc.event;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is published on the MinecraftForge.EVENTBUS bus whenever EiraMoticons reloads it's emoticons.
 * It is also published once during startup.
 * Other mods can listen on this event to register their own emoticons.
 */
public class ReloadEmotes extends Event {
}
