package com.tacz.guns.compat.carryon;

import com.tacz.guns.GunMod;
import net.minecraft.core.registries.BuiltInRegistries;
import tschipp.carryon.common.config.ListHandler;

public class BlackList {
    private static final String CARRY_ON_ID = "carryon";

    public static void addBlackList() {
        BuiltInRegistries.BLOCK.keySet().stream().filter(id -> id.getNamespace().equals(GunMod.MOD_ID))
                .forEach(id -> ListHandler.addForbiddenTiles(id.toString()));
    }
}
