package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IBlockExtension;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow
    @Final
    public Level level;

    @Inject(
            method = "finalizeExplosion",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
            )
    )
    private void tacz$onBlockExploded(boolean spawnParticles, CallbackInfo ci, @Local(ordinal = 0) BlockPos pos, @Local(ordinal = 0) BlockState state) {
        if (state.getBlock() instanceof IBlockExtension block) {
            block.tacz$onBlockExploded(state, this.level, pos, (Explosion) (Object) this);
        }
    }
}