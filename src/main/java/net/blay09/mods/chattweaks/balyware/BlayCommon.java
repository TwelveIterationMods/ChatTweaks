package net.blay09.mods.chattweaks.balyware;

import java.net.URI;

public class BlayCommon {

	public static void openWebLink(URI uri) {
		try {
			Class<?> desktopClass = Class.forName("java.awt.Desktop");
			Object object = desktopClass.getMethod("getDesktop").invoke(null);
			desktopClass.getMethod("browse", URI.class).invoke(object, uri);
		} catch (Throwable ignored) {}
	}

}
