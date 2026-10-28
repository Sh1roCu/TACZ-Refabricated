package com.tacz.guns.event;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.block.GunDisplayCaseBlock;
import com.tacz.guns.block.GunDisplayCaseSlotHelper;
import com.tacz.guns.block.entity.GunDisplayCaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GunDisplayCaseInteractionEvent {
    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (!player.isShiftKeyDown() || player.getMainHandItem().getItem() instanceof IGun) {
            return InteractionResult.PASS;
        }
        if (!(player.getOffhandItem().getItem() instanceof IGun)) {
            return InteractionResult.PASS;
        }
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof GunDisplayCaseBlock displayCaseBlock)) {
            return InteractionResult.PASS;
        }

        InteractionResult result = InteractionResult.PASS;

//        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide()));
//        event.setCanceled(true);
        result = InteractionResult.sidedSuccess(level.isClientSide());
        displayCaseBlock.toggleLockFromInteraction(level, pos, state);

        return result;
    }

    public static InteractionResult onLeftClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
//        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
//            return;
//        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof GunDisplayCaseBlock) || state.getValue(GunDisplayCaseBlock.LOCKED)) {
            return InteractionResult.PASS;
        }
        HitResult picked = player.pick(6.0D, 0.0F, false);
        if (!(picked instanceof BlockHitResult hit) || !hit.getBlockPos().equals(pos)) {
            return InteractionResult.PASS;
        }
        int slot = GunDisplayCaseSlotHelper.getSlot(state, pos, hit.getLocation());
        if (slot < 0) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof GunDisplayCaseBlockEntity displayCase) || displayCase.getItem(slot).isEmpty()) {
            return InteractionResult.PASS;
        }

        InteractionResult result = InteractionResult.PASS;

        // event.setCanceled(true);
        result = InteractionResult.FAIL;
        boolean shouldDrop = !player.isCreative() || player.isShiftKeyDown();
        if (!level.isClientSide() && (shouldDrop ? displayCase.dropItem(slot) : displayCase.removeItem(slot))) {
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return result;
    }
}
