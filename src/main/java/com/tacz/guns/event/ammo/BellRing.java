package com.tacz.guns.event.ammo;

import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class BellRing {
    public static void onAmmoHitBlock(AmmoHitBlockEvent event) {
        Level level = event.getLevel();
        BlockState state = event.getState();
        BlockHitResult hitResult = event.getHitResult();
        if (state.getBlock() instanceof BellBlock bell) {
            bell.attemptToRing(level, hitResult.getBlockPos(), hitResult.getDirection());
        }
    }
}
