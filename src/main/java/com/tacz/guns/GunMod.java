package com.tacz.guns;

import com.tacz.guns.api.resource.ResourceManager;
import com.tacz.guns.compat.kubejs.TimelessKubeJSPlugin;
import com.tacz.guns.init.*;
import com.tacz.guns.resource.GunPackLoader;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GunMod {
    public static final String MOD_ID = "tacz";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    /**
     * 默认模型包文件夹
     */
    public static final String DEFAULT_GUN_PACK_NAME = "tacz_default_gun";

    public static void setup() {
        EnvType side = FabricLoader.getInstance().getEnvironmentType();
        GunPackLoader.INSTANCE.packType = side == EnvType.CLIENT ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA;

        CommonRegistry.onSetupEvent();

        ModBlocks.init();
        ModItems.init();
        ModCreativeTabs.init();
        ModEntities.init();
        ModRecipe.init();
        ModContainer.init();
        ModSounds.init();
        ModParticles.init();
        ModAttributes.init();
        ModPainting.init();
        if (FabricLoader.getInstance().isModLoaded("kubejs")) {
            TimelessKubeJSPlugin.onItemRegister();
        }

        registerDefaultExtraGunPack();
        AttachmentPropertyManager.registerModifier();
    }

    private static void registerDefaultExtraGunPack() {
        String jarDefaultPackPath = String.format("/assets/%s/custom/%s", GunMod.MOD_ID, DEFAULT_GUN_PACK_NAME);
        ResourceManager.registerExportResource(GunMod.class, jarDefaultPackPath);
    }
}
