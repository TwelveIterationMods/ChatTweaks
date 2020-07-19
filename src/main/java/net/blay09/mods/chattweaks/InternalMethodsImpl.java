package net.blay09.mods.chattweaks;

import net.blay09.mods.chattweaks.api.InternalMethods;
import net.blay09.mods.chattweaks.imagepreview.ImageUrlTransformers;

import java.util.function.Function;

public class InternalMethodsImpl implements InternalMethods {
    @Override
    public void registerImageURLTransformer(Function<String, String> urlTransformer) {
        ImageUrlTransformers.registerImageURLTransformer(urlTransformer);
    }
}
