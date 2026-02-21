package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
            com.tacz.guns.util.InputExtraCheck.TACZ_CATEGORY);

    public static void onInteractKeyPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && INTERACT_KEY.matches(new net.minecraft.client.input.KeyEvent(event.getKey(), event.getScanCode(), event.getModifiers()))) {
            doInteractLogic();
        }
    }

    public static void onInteractMousePress(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && INTERACT_KEY.matchesMouse(new net.minecraft.client.input.MouseButtonEvent(0, 0, new net.minecraft.client.input.MouseButtonInfo(event.getButton(), event.getModifiers())))) {
            doInteractLogic();
        }
    }

    public static boolean onInteractControllerPress(boolean isPress) {
        if (isInGame() && isPress) {
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
            if (hitResult instanceof BlockHitResult blockHitResult) {
                interactBlock(blockHitResult, player, mc);
                return true;
            }
            if (hitResult instanceof EntityHitResult entityHitResult) {
                interactEntity(entityHitResult, mc);
                return true;
            }
        }
        return false;
    }

    private static void doInteractLogic() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return;
        }
        HitResult hitResult = mc.hitResult;
        if (hitResult == null) {
            return;
        }
        if (hitResult instanceof BlockHitResult blockHitResult) {
            interactBlock(blockHitResult, player, mc);
            return;
        }
        if (hitResult instanceof EntityHitResult entityHitResult) {
            interactEntity(entityHitResult, mc);
        }
    }

    private static void interactBlock(BlockHitResult blockHitResult, LocalPlayer player, Minecraft mc) {
        BlockPos blockPos = blockHitResult.getBlockPos();
        BlockState block = player.level().getBlockState(blockPos);
        if (InteractKeyConfigRead.canInteractBlock(block)) {
            mc.startUseItem();
        }
    }

    private static void interactEntity(EntityHitResult entityHitResult, Minecraft mc) {
        Entity entity = entityHitResult.getEntity();
        if (InteractKeyConfigRead.canInteractEntity(entity)) {
            mc.startUseItem();
        }
    }
}