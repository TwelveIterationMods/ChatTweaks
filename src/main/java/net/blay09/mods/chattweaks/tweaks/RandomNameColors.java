package net.blay09.mods.chattweaks.tweaks;

import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * TODO
 **/
public class RandomNameColors {

    private static final Random random = new Random();
    private static final TextFormatting[] VALID_COLORS = new TextFormatting[]{
            TextFormatting.DARK_BLUE,
            TextFormatting.DARK_GREEN,
            TextFormatting.DARK_AQUA,
            TextFormatting.DARK_RED,
            TextFormatting.DARK_PURPLE,
            TextFormatting.GOLD,
            TextFormatting.GRAY,
            TextFormatting.BLUE,
            TextFormatting.GREEN,
            TextFormatting.AQUA,
            TextFormatting.RED,
            TextFormatting.LIGHT_PURPLE,
            TextFormatting.YELLOW,
            TextFormatting.WHITE
    };

    private static final Map<String, TextFormatting> nameColorMap = new HashMap<>();

    public static TextFormatting getRandomNameColor(String senderName) {
        TextFormatting color = nameColorMap.get(senderName);
        if (color == null) {
            color = VALID_COLORS[random.nextInt(VALID_COLORS.length)];
            nameColorMap.put(senderName, color);
        }
        return color;
    }

}
