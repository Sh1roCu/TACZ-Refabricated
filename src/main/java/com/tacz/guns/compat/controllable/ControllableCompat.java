package com.tacz.guns.compat.controllable;

import com.tacz.guns.api.item.gun.FireMode;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.ItemStack;

public class ControllableCompat {
    private static final String MOD_ID = "controllable";

    public static void init() {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            ControllableInner.init();
        }
    }

    public static void onGunShoot(ItemStack gunItem, FireMode fireMode) {
        if (FabricLoader.getInstance().isModLoaded(MOD_ID)) {
            ControllableInner.rumbleShoot(gunItem, fireMode);
        }
    }
}
