package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IBlockChange;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class LevelMixin {
    @WrapOperation(
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;onBlockStateChange(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)V"
            )
    )
    private void tacz$onBlockStateChange(Level instance, BlockPos pos, BlockState blockState, BlockState newState, Operation<Void> original,
                                         @Local(argsOnly = true) BlockState oriState) {
        original.call(instance, pos, blockState, newState);
        if (oriState.getBlock() instanceof IBlockChange custom) {
            custom.onBlockStateChange((Level) (Object) this, pos, blockState, oriState);
        }
    }
}
