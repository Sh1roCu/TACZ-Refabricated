package cn.sh1rocu.tacz.mixin.accessor;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerExplosion.class)
public interface ExplosionAccessor {
    @Accessor("damageSource")
    DamageSource tacz$getDamageSource();
}
