package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.entity.TargetMinecart;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class ModEntities {
    public static void init() {

    }

    public static EntityType<EntityKineticBullet> BULLET = register("bullet", EntityKineticBullet.TYPE);
    public static EntityType<TargetMinecart> TARGET_MINECART = register("target_minecart", TargetMinecart.TYPE);

    private static <T extends Entity> EntityType<T> register(String name, EntityType<T> type) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name), type);
    }
}