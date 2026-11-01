package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

public class ModPainting {
    public static void init() {

    }

    public static final PaintingVariant BLOOD_STRIKE_1 = register("blood_strike_1", new PaintingVariant(32, 32));
//    public static final PaintingVariant BLOOD_STRIKE_2 = register("blood_strike_2",  new PaintingVariant(32, 32));

    private static PaintingVariant register(String name, PaintingVariant painting) {
        return Registry.register(BuiltInRegistries.PAINTING_VARIANT, new ResourceLocation(GunMod.MOD_ID, name), painting);
    }
}
