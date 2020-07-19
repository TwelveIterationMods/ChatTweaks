package net.blay09.mods.chattweaks.imagepreview;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.api.ChatComponentClickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = ChatTweaks.MOD_ID, value = Dist.CLIENT)
public class ImagePreviewHandler {

    @SubscribeEvent
    public static void onChatComponentClicked(ChatComponentClickEvent event) {
        final ClickEvent clickEvent = event.getStyle().getClickEvent();
        if (clickEvent != null) {
            if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                String url = clickEvent.getValue();
                String directURL = null;
                for (Function<String, String> function : ImageUrlTransformers.getImageURLTransformers()) {
                    directURL = function.apply(url);
                    if (directURL != null) {
                        break;
                    }
                }

                if (directURL != null) {
                    try {
                        Minecraft.getInstance().displayGuiScreen(new ImagePreviewScreen(Minecraft.getInstance().currentScreen, new URL(url), new URL(directURL)));
                        event.setCanceled(true);
                    } catch (MalformedURLException e) {
                        ChatTweaks.logger.error("Could not open image preview: ", e);
                    }
                }
            }
        }
    }

}
