package net.blay09.mods.chattweaks.util;

import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

public class PasswordStringFormatter implements Function<String, String> {

    @Override
    public String apply(String text) {
        return StringUtils.repeat('*', text.length());
    }

}
