package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.*;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
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

    public static BlockEntityType<GunSmithTableBlockEntity> GUN_SMITH_TABLE_BE = registerBlockEntity("gun_smith_table", GunSmithTableBlockEntity.TYPE);
    public static BlockEntityType<TargetBlockEntity> TARGET_BE = registerBlockEntity("target", TargetBlockEntity.TYPE);
    public static BlockEntityType<StatueBlockEntity> STATUE_BE = registerBlockEntity("statue", StatueBlockEntity.TYPE);

    public static final TagKey<Block> BULLET_IGNORE_BLOCKS = TagKey.create(Registries.BLOCK, new ResourceLocation(GunMod.MOD_ID, "bullet_ignore"));

    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(GunMod.MOD_ID, name), block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntity) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(GunMod.MOD_ID, name), blockEntity);
    }
}
