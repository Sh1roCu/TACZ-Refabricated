package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import com.tacz.guns.util.InputExtraCheck;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ShootKey {
    public static final KeyMapping SHOOT_KEY = new KeyMapping("key.tacz.shoot.desc",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            InputExtraCheck.TACZ_CATEGORY);
    private static boolean lastTimeShootSuccess = false;

    public static void autoShoot(Minecraft mc, boolean isPhaseEnd) {
        if (!isPhaseEnd && !isInGame()) {
            return;
        }
        LocalPlayerSprint.stopSprint = false;

        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstAuto = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
            if (SHOOT_KEY.isDown()) {
                // 能开火时禁止冲刺
                LocalPlayerSprint.stopSprint = true;

                if (fireMode != FireMode.AUTO && !isBurstAuto && lastTimeShootSuccess) {
                    // 非全自动情况，禁止连续开火
                    return;
                }
                if (operator.shoot() == ShootResult.SUCCESS) {
                    lastTimeShootSuccess = true;
                }
            } else {
                lastTimeShootSuccess = false;
            }
        }
    }

    public static boolean autoShootController() {
        if (!isInGame()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstAuto = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
            if (fireMode == FireMode.AUTO || isBurstAuto) {
                return operator.shoot() == ShootResult.SUCCESS;
            }
        }
        return false;
    }

    public static void semiShoot(InputEvent.MouseButton.Post event) {
        if (isInGame() && SHOOT_KEY.matchesMouse(new net.minecraft.client.input.MouseButtonEvent(0, 0, new net.minecraft.client.input.MouseButtonInfo(event.getButton(), event.getModifiers())))) {
            // 松开鼠标，重置 DryFire 状态
            if (event.getAction() == GLFW.GLFW_RELEASE) {
                SoundPlayManager.resetDryFireSound();
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null || player.isSpectator()) {
                return;
            }
            ItemStack mainHandItem = player.getMainHandItem();
            if (mainHandItem.getItem() instanceof IGun iGun) {
                FireMode fireMode = iGun.getFireMode(mainHandItem);
                boolean isBurstSemi = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                        .map(index -> !index.getGunData().getBurstData().isContinuousShoot())
                        .orElse(false);
                if (fireMode == FireMode.UNKNOWN) {
                    player.displayClientMessage(Component.translatable("message.tacz.fire_select.fail"), false);
                }
                if (fireMode == FireMode.SEMI || isBurstSemi) {
                    lastTimeShootSuccess = IClientPlayerGunOperator.fromLocalPlayer(player).shoot() == ShootResult.SUCCESS;
                }
            }
        }
    }

    public static boolean semiShootController(boolean isPress) {
        if (!isInGame()) {
            return false;
        }
        // 松开鼠标，重置 DryFire 状态
        if (!isPress) {
            SoundPlayManager.resetDryFireSound();
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.getItem() instanceof IGun iGun) {
            FireMode fireMode = iGun.getFireMode(mainHandItem);
            boolean isBurstSemi = fireMode == FireMode.BURST && TimelessAPI.getCommonGunIndex(iGun.getGunId(mainHandItem))
                    .map(index -> !index.getGunData().getBurstData().isContinuousShoot())
                    .orElse(false);
            if (fireMode == FireMode.UNKNOWN) {
                player.displayClientMessage(Component.translatable("message.tacz.fire_select.fail"), false);
                return false;
            }
            if (fireMode == FireMode.SEMI || isBurstSemi) {
                return IClientPlayerGunOperator.fromLocalPlayer(player).shoot() == ShootResult.SUCCESS;
            }
        }
        return false;
    }
}
