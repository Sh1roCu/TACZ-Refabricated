package cn.sh1rocu.tacz.api.extension;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

public interface IMinecart {
    default boolean tacz$canBeRidden() {
        return ((AbstractMinecart) this).isRideable();
    }
}
