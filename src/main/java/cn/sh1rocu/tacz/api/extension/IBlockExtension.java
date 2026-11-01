package cn.sh1rocu.tacz.api.extension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockExtension {
    default void tacz$onBlockExploded(BlockState state, Level world, BlockPos pos, Explosion explosion) {
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        ((Block) this).wasExploded(world, pos, explosion);
    }
}