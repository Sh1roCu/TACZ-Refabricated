package com.tacz.guns.config;

import com.tacz.guns.GunMod;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class PreLoadConfig {
    public static void init() {
        ConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.COMMON, spec, "tacz-pre.toml");
    }

    private static ModConfigSpec spec;
    public static ModConfigSpec.BooleanValue override;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("gunpack");
        builder.comment("When enabled, the mod will not try to overwrite the default pack under .minecraft/tacz\n" +
                "Since 1.0.4, the overwriting will only run when you start client or a dedicated server");
        override = builder.define("DefaultPackDebug", false);
        builder.pop();
        spec = builder.build();
    }

//    public static PreLoadModConfig getModConfig() {
//        var c = new PreLoadModConfig(ModConfig.Type.COMMON, spec, GunMod.MOD_ID, "tacz-pre.toml");
//        // 从 ConfigTracker 中移除，防止从默认文件夹重复加载
//        ConfigTracker.INSTANCE.configSets().get(ModConfig.Type.COMMON).remove(c);
//        ConfigTracker.INSTANCE.fileMap().remove(c.getFileName(), c);
//        return c;
//    }
//
//    public static void load(Path configBasePath) {
//        if (spec.isLoaded()) return;
//        PreLoadModConfig config = getModConfig();
//        final CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
//        config.setConfigData(configData);
//        config.fireEvent(config);
//        config.save();
//    }
}
