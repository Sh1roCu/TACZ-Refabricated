package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.item.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ModItems {
    public static void init() {
        GunItemManager.registerGunItem(ModernKineticGunItem.TYPE_NAME, MODERN_KINETIC_GUN);
    }

    public static ModernKineticGunItem MODERN_KINETIC_GUN = register("modern_kinetic_gun", new ModernKineticGunItem());

//    public static ThrowableItem M67 = register("m67", new ThrowableItem());

    public static Item AMMO = register("ammo", new AmmoItem());
    public static AttachmentItem ATTACHMENT = register("attachment", new AttachmentItem());

    public static GunSmithTableItem GUN_SMITH_TABLE = register("gun_smith_table", new DefaultTableItem(ModBlocks.GUN_SMITH_TABLE));
    public static GunSmithTableItem WORKBENCH_111 = register("workbench_a", new GunSmithTableItem(ModBlocks.WORKBENCH_111));
    public static GunSmithTableItem WORKBENCH_211 = register("workbench_b", new GunSmithTableItem(ModBlocks.WORKBENCH_211));
    public static GunSmithTableItem WORKBENCH_121 = register("workbench_c", new GunSmithTableItem(ModBlocks.WORKBENCH_121));


    public static Item TARGET = register("target", new BlockItem(ModBlocks.TARGET, new FabricItemSettings()));
    public static Item STATUE = register("statue", new BlockItem(ModBlocks.STATUE, new FabricItemSettings()));
    public static Item AMMO_BOX = register("ammo_box", new AmmoBoxItem());
    public static Item TARGET_MINECART = register("target_minecart", new TargetMinecartItem());

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GunMod.MOD_ID, name), item);
    }
}