package net.blay09.mods.chattweaks.compat;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.gui.screen.ExtendedChatScreen;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;

public class BlurCompat {

    public static void enableBlurCompat() {
        Object instance = ModList.get().getModObjectById(Compat.BLUR);
        if (instance != null) {
            try {
                Field blurExclusionsField = instance.getClass().getDeclaredField("blurExclusions");
                blurExclusionsField.setAccessible(true);
                String[] blurExclusions = (String[]) blurExclusionsField.get(instance);
                blurExclusions = ArrayUtils.add(blurExclusions, ExtendedChatScreen.class.getName());
                blurExclusionsField.set(instance, blurExclusions);
            } catch (Throwable e) {
                ChatTweaks.logger.error("Failed to insert " + ExtendedChatScreen.class.getName() + " into Blur exclusions list. Please add it manually to prevent blur on chat.");
                e.printStackTrace();
            }

        }
    }

}
