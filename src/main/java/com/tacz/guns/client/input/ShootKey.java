package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.ShootResult;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.client.gameplay.LocalPlayerSprint;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.compat.controllable.ControllableCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ShootKey {
    public static final KeyMapping SHOOT_KEY = new KeyMapping("key.tacz.shoot.desc",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_LEFT,
            "key.category.tacz");
    private static boolean lastTimeShootSuccess = false;
    private static boolean controllerShootDown = false;

    public static void autoShoot(Minecraft mc, boolean isPhaseEnd) {
        if (!isPhaseEnd || !isInGame()) {
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
            boolean isShootDown = SHOOT_KEY.isDown() || controllerShootDown;
            boolean canContinuouslyShoot = fireMode == FireMode.AUTO || isBurstAuto;
            boolean shouldCharge = isShootDown && (canContinuouslyShoot || !lastTimeShootSuccess);
            if (operator.chargeShoot(shouldCharge)) {
                LocalPlayerSprint.stopSprint = true;
                if (!canContinuouslyShoot && lastTimeShootSuccess) {
                    // 非全自动情况，禁止连续开火，也不应在按住上一枪扳机时继续蓄力
                    return;
                }
                if (operator.shoot() == ShootResult.SUCCESS) {
                    lastTimeShootSuccess = true;
                    ControllableCompat.onGunShoot(mainHandItem, fireMode);
                }
            }
            if (isShootDown) {
                LocalPlayerSprint.stopSprint = true;
            } else {
                lastTimeShootSuccess = false;
                SoundPlayManager.resetDryFireSound();
            }
        }
    }

    public static boolean shootControllerTick(boolean isShootDown) {
        controllerShootDown = isShootDown;
        return false;
    }
}
