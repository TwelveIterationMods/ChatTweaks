package net.blay09.mods.chattweaks.api;

import java.util.function.Function;

public class ChatTweaksAPI {

    public static InternalMethods __internalMethods;

    public static void registerImageURLTransformer(Function<String, String> urlTransformer) {
        __internalMethods.registerImageURLTransformer(urlTransformer);
    }
}
