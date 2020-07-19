package net.blay09.mods.chattweaks.chat;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.SleepInMultiplayerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class ChatScreenReplacementHandler {

    @SubscribeEvent
    public static void onOpenGui(GuiOpenEvent event) {
        if (event.getGui() != null) {
            if (event.getGui().getClass() == ChatScreen.class) {
                event.setGui(new ExtendedChatScreen(((ChatScreen) event.getGui())));
            } else if (event.getGui().getClass() == SleepInMultiplayerScreen.class) {
                //event.setGui(new ExtendedSleepScreen());
            }
        }
    }

}
