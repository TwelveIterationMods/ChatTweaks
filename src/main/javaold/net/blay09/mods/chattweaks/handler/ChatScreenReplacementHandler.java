package net.blay09.mods.chattweaks.handler;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.gui.screen.ExtendedChatScreen;
import net.blay09.mods.chattweaks.gui.screen.ExtendedSleepScreen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID)
public class ChatScreenReplacementHandler {

    /* TODO @SubscribeEvent
    public void onConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft.getInstance().ingameGUI.persistantChatGUI = persistentChatGUI;
    }*/

    @SubscribeEvent
    public void onOpenGui(GuiOpenEvent event) {
        if (event.getGui() != null) {
            if (event.getGui().getClass() == ChatScreen.class) {
                event.setGui(new ExtendedChatScreen(((ChatScreen) event.getGui()).defaultInputFieldText));
            } else if (event.getGui().getClass() == SleepInMultiplayerScreen.class) {
                event.setGui(new ExtendedSleepScreen());
            }
        }
    }

}
