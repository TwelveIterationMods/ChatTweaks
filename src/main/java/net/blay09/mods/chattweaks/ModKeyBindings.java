package net.blay09.mods.chattweaks;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyBinding keySwitchChatView = new KeyBinding("key.chattweaks.switch_chat_view", KeyConflictContext.GUI, KeyModifier.SHIFT, InputMappings.getInputByCode(GLFW.GLFW_KEY_TAB, 0), "key.categories.chattweaks");

    public static void register() {
        ClientRegistry.registerKeyBinding(keySwitchChatView);
    }
}
