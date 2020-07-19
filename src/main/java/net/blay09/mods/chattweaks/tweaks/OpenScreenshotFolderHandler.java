package net.blay09.mods.chattweaks.tweaks;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.ChatComponentClickEvent;
import net.blay09.mods.chattweaks.util.DesktopUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class OpenScreenshotFolderHandler {

    @SubscribeEvent
    public static void onOpenScreenshotFolder(ChatComponentClickEvent event) {
        ClickEvent clickEvent = event.getStyle().getClickEvent();
        if (clickEvent == null || clickEvent.getAction() != ClickEvent.Action.OPEN_FILE || !clickEvent.getValue().endsWith(".png")) {
            return;
        }

        if (Screen.hasShiftDown()) {
            if (DesktopUtils.openContainingFolder(new File(clickEvent.getValue()))) {
                event.setCanceled(true);
            }
        }
    }

}
