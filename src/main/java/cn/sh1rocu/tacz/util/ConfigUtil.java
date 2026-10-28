package cn.sh1rocu.tacz.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

public class ConfigUtil {
    private ConfigUtil() {
    }

    private static final Splitter DOT_SPLITTER = Splitter.on(".");

    private static List<String> split(String path) {
        return Lists.newArrayList(DOT_SPLITTER.split(path));
    }

    public static <T> ForgeConfigSpec.ConfigValue<List<? extends T>> defineListAllowEmpty(ForgeConfigSpec.Builder builder, String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return defineListAllowEmpty(builder, split(path), defaultValue, elementValidator);
    }

    public static <T> ForgeConfigSpec.ConfigValue<List<? extends T>> defineListAllowEmpty(ForgeConfigSpec.Builder builder, List<String> path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
        return builder.defineListAllowEmpty(path, () -> defaultValue, elementValidator);
    }
}
