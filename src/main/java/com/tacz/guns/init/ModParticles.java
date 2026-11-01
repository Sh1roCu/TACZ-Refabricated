package com.tacz.guns.init;

import com.mojang.serialization.Codec;
import com.tacz.guns.GunMod;
import com.tacz.guns.particles.BulletHoleOption;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ModParticles {
    public static void init() {

    }

    public static final ParticleType<BulletHoleOption> BULLET_HOLE = register("bullet_hole", new ModParticleType<>(false, BulletHoleOption.DESERIALIZER, BulletHoleOption.CODEC));

    private static <T extends ParticleOptions> ParticleType<T> register(String name, ParticleType<T> type) {
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(GunMod.MOD_ID, name), type);
    }

    @SuppressWarnings("deprecation")
    private static class ModParticleType<T extends ParticleOptions> extends ParticleType<T> {
        private final Codec<T> codec;

        public ModParticleType(boolean overrideLimiter, ParticleOptions.Deserializer<T> deserializer, Codec<T> codec) {
            super(overrideLimiter, deserializer);
            this.codec = codec;
        }

        @Override
        public @NotNull Codec<T> codec() {
            return this.codec;
        }
    }
}
