package com.tacz.guns.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.tacz.guns.GunMod;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class PreLoadConfig {
    public static void init() {
        ForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.COMMON, spec, "tacz-pre.toml");

    }

    private static ForgeConfigSpec spec;
    public static ForgeConfigSpec.BooleanValue override;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("gunpack");
        builder.comment("When enabled, the mod will not try to overwrite the default pack under .minecraft/tacz\n" +
                "Since 1.0.4, the overwriting will only run when you start client or a dedicated server");
        override = builder.define("DefaultPackDebug", false);
        builder.pop();
        spec = builder.build();
    }

    public static PreLoadModConfig getModConfig() {
        var c = new PreLoadModConfig(ModConfig.Type.COMMON, spec, GunMod.MOD_ID, "tacz-pre.toml");
        // 从 ConfigTracker 中移除，防止从默认文件夹重复加载
        ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.COMMON).remove(c);
        ConfigTracker.INSTANCE.fileMap().remove(c.getFileName(), c);
        return c;
    }

    public static void load(Path configBasePath) {
        if (spec.isLoaded()) return;
        PreLoadModConfig config = getModConfig();
        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        config.fireEvent(config);
        config.save();
    }
}
