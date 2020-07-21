package net.blay09.mods.chattweaks.compat;

import net.blay09.mods.chattweaks.ChatTweaks;
import net.blay09.mods.chattweaks.chat.screen.ExtendedChatScreen;
import net.blay09.mods.chattweaks.chat.screen.ExtendedSleepScreen;
import net.minecraftforge.fml.ModList;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;

public class BlurCompat {

    private static final String BLUR_MOD_ID = "blur";

    public static void enableBlurCompat() {
        Object instance = ModList.get().getModObjectById(BLUR_MOD_ID);
        if (instance != null) {
            try {
                Field blurExclusionsField = instance.getClass().getDeclaredField("blurExclusions");
                blurExclusionsField.setAccessible(true);
                String[] blurExclusions = (String[]) blurExclusionsField.get(instance);
                blurExclusions = ArrayUtils.add(blurExclusions, ExtendedChatScreen.class.getName());
                blurExclusions = ArrayUtils.add(blurExclusions, ExtendedSleepScreen.class.getName());
                blurExclusionsField.set(instance, blurExclusions);
            } catch (Throwable e) {
                ChatTweaks.logger.error("Failed to insert " + ExtendedChatScreen.class.getName() + " and/or " + ExtendedSleepScreen.class.getName() + " into Blur exclusions list. Please add it manually to prevent blur on chat.");
                e.printStackTrace();
            }
        }
    }

}
