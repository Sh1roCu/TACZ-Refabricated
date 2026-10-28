package com.tacz.guns.event;

import com.tacz.guns.block.GunPegboardBlock;
import com.tacz.guns.block.GunPegboardSlotHelper;
import com.tacz.guns.block.entity.GunPegboardBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class GunPegboardInteractionEvent {
    public static InteractionResult onLeftClickBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
        if (player.getMainHandItem().is(Items.DEBUG_STICK)) return InteractionResult.PASS;
//        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START) {
//            return;
//        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof GunPegboardBlock)) {
            return InteractionResult.PASS;
        }
        HitResult picked = player.pick(6.0D, 0.0F, false);
        if (!(picked instanceof BlockHitResult hit) || !hit.getBlockPos().equals(pos)) {
            return InteractionResult.PASS;
        }
        int slot = GunPegboardSlotHelper.getSlot(state, pos, hit.getLocation());
        if (slot < 0) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof GunPegboardBlockEntity pegboard) || pegboard.getItem(slot).isEmpty()) {
            return InteractionResult.PASS;
        }

        InteractionResult result = InteractionResult.PASS;
        // event.setCanceled(true);
        result = InteractionResult.FAIL;
        boolean shouldDrop = !player.isCreative() || player.isShiftKeyDown();
        if (!level.isClientSide() && (shouldDrop ? pegboard.dropItem(slot) : pegboard.removeItem(slot))) {
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return result;
    }
}
