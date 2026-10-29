package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.*;
import com.tacz.guns.block.entity.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlocks {
    public static void init() {

    }

    // 旧方块就让他独占一个了
    public static Block GUN_SMITH_TABLE = registerBlock("gun_smith_table", new GunSmithTableBlockB());
    public static Block WORKBENCH_111 = registerBlock("workbench_a", new GunSmithTableBlockA());
    public static Block WORKBENCH_211 = registerBlock("workbench_b", new GunSmithTableBlockB());
    public static Block WORKBENCH_121 = registerBlock("workbench_c", new GunSmithTableBlockC());

    public static Block TARGET = registerBlock("target", new TargetBlock());
    public static Block STATUE = registerBlock("statue", new StatueBlock());
    public static Block GUN_PEGBOARD = registerBlock("gun_pegboard", new GunPegboardBlock());
    public static Block GLOW_GUN_PEGBOARD = registerBlock("glow_gun_pegboard", new GunPegboardBlock(true));
    public static Block AMMO_STORAGE = registerBlock("ammo_storage", new AmmoStorageBlock(AmmoStorageBlock.IRON_CAPACITY_STACKS));
    public static Block AMMO_STORAGE_GOLD = registerBlock("ammo_storage_gold", new AmmoStorageBlock(AmmoStorageBlock.GOLD_CAPACITY_STACKS));
    public static Block AMMO_STORAGE_DIAMOND = registerBlock("ammo_storage_diamond", new AmmoStorageBlock(AmmoStorageBlock.DIAMOND_CAPACITY_STACKS));
    public static Block GUN_DISPLAY_CASE = registerBlock("gun_display_case", new GunDisplayCaseBlock());

    public static BlockEntityType<GunSmithTableBlockEntity> GUN_SMITH_TABLE_BE = registerBlockEntity("gun_smith_table", GunSmithTableBlockEntity.TYPE);
    public static BlockEntityType<TargetBlockEntity> TARGET_BE = registerBlockEntity("target", TargetBlockEntity.TYPE);
    public static BlockEntityType<StatueBlockEntity> STATUE_BE = registerBlockEntity("statue", StatueBlockEntity.TYPE);
    public static BlockEntityType<GunPegboardBlockEntity> GUN_PEGBOARD_BE = registerBlockEntity("gun_pegboard", GunPegboardBlockEntity.TYPE);
    public static BlockEntityType<AmmoStorageBlockEntity> AMMO_STORAGE_BE = registerBlockEntity("ammo_storage", AmmoStorageBlockEntity.TYPE);
    public static BlockEntityType<GunDisplayCaseBlockEntity> GUN_DISPLAY_CASE_BE = registerBlockEntity("gun_display_case", GunDisplayCaseBlockEntity.TYPE);

    public static final TagKey<Block> BULLET_IGNORE_BLOCKS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "bullet_ignore"));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, name), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntity) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, name), blockEntity);
    }
}
