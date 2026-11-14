package com.tacz.guns.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class DecorationLock {
    public static final BooleanProperty LOCKED = BooleanProperty.create("decoration_locked");

    private DecorationLock() {
    }
}
