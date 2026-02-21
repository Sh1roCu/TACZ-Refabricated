package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.item.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class ModItems {
    public static void init() {
        GunItemManager.registerGunItem(ModernKineticGunItem.TYPE_NAME, MODERN_KINETIC_GUN);
    }

    public static ModernKineticGunItem MODERN_KINETIC_GUN = register("modern_kinetic_gun", p -> new ModernKineticGunItem(p.stacksTo(1)));

//    public static ThrowableItem M67 = register("m67", p -> new ThrowableItem(p.stacksTo(1)));

    public static Item AMMO = register("ammo", p -> new AmmoItem(p.stacksTo(1)));
    public static AttachmentItem ATTACHMENT = register("attachment", p -> new AttachmentItem(p.stacksTo(1)));

    public static GunSmithTableItem GUN_SMITH_TABLE = register("gun_smith_table", p -> new DefaultTableItem(ModBlocks.GUN_SMITH_TABLE, p.stacksTo(1)));
    public static GunSmithTableItem WORKBENCH_111 = register("workbench_a", p -> new GunSmithTableItem(ModBlocks.WORKBENCH_111, p.stacksTo(1)));
    public static GunSmithTableItem WORKBENCH_211 = register("workbench_b", p -> new GunSmithTableItem(ModBlocks.WORKBENCH_211, p.stacksTo(1)));
    public static GunSmithTableItem WORKBENCH_121 = register("workbench_c", p -> new GunSmithTableItem(ModBlocks.WORKBENCH_121, p.stacksTo(1)));


    public static Item TARGET = register("target", p -> new BlockItem(ModBlocks.TARGET, p));
    public static Item STATUE = register("statue", p -> new BlockItem(ModBlocks.STATUE, p));
    public static Item AMMO_BOX = register("ammo_box", p -> new AmmoBoxItem(p.stacksTo(1)));
    public static Item TARGET_MINECART = register("target_minecart", p -> new TargetMinecartItem(p.stacksTo(1)));

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory) {
        Identifier id = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        T item = factory.apply(new Item.Properties().setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }
}
