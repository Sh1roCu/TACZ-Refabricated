package com.tacz.guns.event;

import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.block.AmmoStorageBlock;
import com.tacz.guns.block.entity.AmmoStorageBlockEntity;
import com.tacz.guns.util.AmmoStorageInteractionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AmmoStorageInteractionEvent {
    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown() || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (!(held.getItem() instanceof IAmmoBox ammoBox)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof AmmoStorageBlock)) {
            return InteractionResult.PASS;
        }

        InteractionResult result = InteractionResult.PASS;

        if (level.isClientSide()) {
//            event.setCancellationResult(InteractionResult.SUCCESS);
//            event.setCanceled(true);
            result = InteractionResult.SUCCESS;
            return result;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof AmmoStorageBlockEntity storage)) {
            return InteractionResult.PASS;
        }
        result = AmmoStorageInteractionHelper.unloadAmmoBox(storage, held, ammoBox, player);
//        event.setCancellationResult(result);
//        event.setCanceled(true);
        return result;
    }
}
