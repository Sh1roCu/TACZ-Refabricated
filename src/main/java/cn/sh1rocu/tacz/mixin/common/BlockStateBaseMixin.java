package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IPistonPushReaction;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @ModifyReturnValue(method = "getPistonPushReaction", at = @At("RETURN"))
    private PushReaction tacz$getPistonPushReaction(PushReaction original) {
        if (getBlock() instanceof IPistonPushReaction custom) {
            PushReaction reaction = custom.getPistonPushReaction(asState());
            if (reaction != null) return reaction;
        }
        return original;
    }
}
