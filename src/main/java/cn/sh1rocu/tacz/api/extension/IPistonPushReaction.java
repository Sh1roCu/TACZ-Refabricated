package cn.sh1rocu.tacz.api.extension;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public interface IPistonPushReaction {
    PushReaction getPistonPushReaction(BlockState state);
}
