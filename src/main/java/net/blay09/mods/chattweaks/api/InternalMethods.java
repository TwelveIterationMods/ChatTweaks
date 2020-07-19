package net.blay09.mods.chattweaks.api;

import java.util.function.Function;

public interface InternalMethods {
    void registerImageURLTransformer(Function<String, String> urlTransformer);
}
