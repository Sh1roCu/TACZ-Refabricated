package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.block.AmmoStorageBlock;
import com.tacz.guns.block.GunDisplayCaseBlock;
import com.tacz.guns.client.event.GunDisplayCaseClientInteractionEvent;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import com.tacz.guns.network.message.ClientMessageAmmoStorageInteract;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class InteractKey {
    public static final KeyMapping INTERACT_KEY = new KeyMapping("key.tacz.interact.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.category.tacz");

    public static boolean onInteractControllerPress(boolean isPress) {
        return isPress && onInteractPress(true);
    }

    public static boolean onInteractPress() {
        return onInteractPress(false);
    }

    public static boolean sharesUseBinding(KeyMapping useKey) {
        return !useKey.isUnbound()
                && KeyBindingHelper.getBoundKeyOf(INTERACT_KEY).equals(KeyBindingHelper.getBoundKeyOf(useKey))
                /*&& INTERACT_KEY.getKeyModifier() == useKey.getKeyModifier()*/;
    }

    private static boolean onInteractPress(boolean fromController) {
        if (!isInGame()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return false;
        }
        HitResult hitResult = mc.hitResult;
        if (hitResult == null) {
            return false;
        }
        boolean vanillaHandlesUse = !fromController && sharesUseBinding(mc.options.keyUse);
        if (hitResult instanceof BlockHitResult blockHitResult) {
            interactBlock(blockHitResult, player, mc, vanillaHandlesUse);
            return true;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            interactEntity(entityHitResult, mc, vanillaHandlesUse);
            return true;
        }
        return false;
    }

    private static void interactBlock(BlockHitResult blockHitResult, LocalPlayer player, Minecraft mc, boolean vanillaHandlesUse) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState block = player.level().getBlockState(blockPos);
        if (block.getBlock() instanceof GunDisplayCaseBlock) {
            GunDisplayCaseClientInteractionEvent.showHintForTarget(mc, true);
        }
        if (block.getBlock() instanceof AmmoStorageBlock) {
            ClientPlayNetworking.send(new ClientMessageAmmoStorageInteract(blockPos));
            return;
        }
        if (!vanillaHandlesUse && InteractKeyConfigRead.canInteractBlock(block)) {
            mc.startUseItem();
        }
    }

    private static void interactEntity(EntityHitResult entityHitResult, Minecraft mc, boolean vanillaHandlesUse) {
        Entity entity = entityHitResult.getEntity();
        if (!vanillaHandlesUse && InteractKeyConfigRead.canInteractEntity(entity)) {
            mc.startUseItem();
        }
    }
}
