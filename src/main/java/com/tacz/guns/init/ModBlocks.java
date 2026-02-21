package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.*;
import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class ModBlocks {
    public static void init() {

    }

    // 旧方块就让他独占一个了
    public static Block GUN_SMITH_TABLE = registerBlock("gun_smith_table", GunSmithTableBlockB::new, woodProps());
    public static Block WORKBENCH_111 = registerBlock("workbench_a", GunSmithTableBlockA::new, woodProps());
    public static Block WORKBENCH_211 = registerBlock("workbench_b", GunSmithTableBlockB::new, woodProps());
    public static Block WORKBENCH_121 = registerBlock("workbench_c", GunSmithTableBlockC::new, woodProps());

    public static Block TARGET = registerBlock("target", TargetBlock::new, woodProps());
    public static Block STATUE = registerBlock("statue", StatueBlock::new, stoneProps());

    public static BlockEntityType<GunSmithTableBlockEntity> GUN_SMITH_TABLE_BE = registerBlockEntity("gun_smith_table", GunSmithTableBlockEntity.TYPE);
    public static BlockEntityType<TargetBlockEntity> TARGET_BE = registerBlockEntity("target", TargetBlockEntity.TYPE);
    public static BlockEntityType<StatueBlockEntity> STATUE_BE = registerBlockEntity("statue", StatueBlockEntity.TYPE);

    public static final TagKey<Block> BULLET_IGNORE_BLOCKS = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "bullet_ignore"));

    private static BlockBehaviour.Properties woodProps() {
        return BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(2.0F, 3.0F).noOcclusion().pushReaction(PushReaction.DESTROY);
    }

    private static BlockBehaviour.Properties stoneProps() {
        return BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(2.0F, 3.0F).noOcclusion().pushReaction(PushReaction.DESTROY);
    }

    private static <T extends Block> T registerBlock(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties properties) {
        Identifier id = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        T block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name, BlockEntityType<T> blockEntity) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name), blockEntity);
    }
}
