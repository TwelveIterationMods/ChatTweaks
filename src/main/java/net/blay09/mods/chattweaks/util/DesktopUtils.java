package net.blay09.mods.chattweaks.util;

import org.apache.commons.lang3.SystemUtils;

import java.awt.*;
import java.io.File;

public class DesktopUtils {

    public static boolean openContainingFolder(File file) {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
                return true;
            } else if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file.getParentFile());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
