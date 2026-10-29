package cn.sh1rocu.tacz.mixin.common.accessor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Explosion.class)
public interface ExplosionAccessor {
    @Accessor("damageSource")
    DamageSource tacz$getDamageSource();
}
