package cn.sh1rocu.tacz.api.extension;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockChange {
    void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState);
}
