package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    public static void init() {

    }

    public static final SoundEvent GUN = register("gun", SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "gun")));
    public static final SoundEvent TARGET_HIT = register("target_block_hit", SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "target_block_hit")));

    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name), soundEvent);
    }
}
