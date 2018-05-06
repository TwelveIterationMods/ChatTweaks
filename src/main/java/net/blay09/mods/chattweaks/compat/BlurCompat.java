package net.blay09.mods.chattweaks.compat;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.gui.chat.GuiChatExt;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;

public class BlurCompat {

    public static void enableBlurCompat() {
        Object instance = Loader.instance().getIndexedModList().get(Compat.BLUR).getMod();
        if (instance != null) {
            try {
                Field blurExclusionsField = instance.getClass().getDeclaredField("blurExclusions");
                blurExclusionsField.setAccessible(true);
                String[] blurExclusions = (String[]) blurExclusionsField.get(instance);
                blurExclusions = ArrayUtils.add(blurExclusions, GuiChatExt.class.getName());
                blurExclusionsField.set(instance, blurExclusions);
            } catch (Throwable e) {
                ChatTweaks.logger.error("Failed to insert " + GuiChatExt.class.getName() + " into Blur exclusions list. Please add it manually to prevent blur on chat.");
                e.printStackTrace();
            }

        }
    }

}
