package com.tacz.guns.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.tacz.guns.GunMod;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public class PreLoadConfig {
    public static void init() {
        NeoForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.COMMON, spec, "tacz-pre.toml");
    }

    private static ModConfigSpec spec;
    public static ModConfigSpec.BooleanValue override;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("gunpack");
        builder.comment("Enable to preserve local default gun pack edits.",
                "When disabled, changes to the TACZ version or bundled resources trigger replacement at startup.",
                "To regenerate, disable this option, delete <game directory>/tacz/tacz_default_gun, then restart.");
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

    public static void save() { spec.save(); }
}
