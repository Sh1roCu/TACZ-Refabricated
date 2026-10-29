package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.AmmoStorageMenu;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModContainer {
    public static void init() {

    }

    public static final MenuType<GunSmithTableMenu> GUN_SMITH_TABLE_MENU = register("gun_smith_table_menu", GunSmithTableMenu.TYPE);
    public static final MenuType<AmmoStorageMenu> AMMO_STORAGE_MENU = register("ammo_storage_menu",
            new ExtendedScreenHandlerType<>((syncId, inventory, data) -> new AmmoStorageMenu(syncId, inventory),
                    BlockPos.STREAM_CODEC));

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType<T> type) {
        return Registry.register(BuiltInRegistries.MENU, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, name), type);
    }
}
