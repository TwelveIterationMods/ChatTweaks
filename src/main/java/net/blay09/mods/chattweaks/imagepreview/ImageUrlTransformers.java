package net.blay09.mods.chattweaks.imagepreview;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

public class ImageUrlTransformers {

    private static final List<Function<String, String>> imageURLTransformers = Lists.newArrayList();

    public static List<Function<String, String>> getImageURLTransformers() {
        return imageURLTransformers;
    }

    public static void registerImageURLTransformer(Function<String, String> function) {
        imageURLTransformers.add(function);
    }

}
