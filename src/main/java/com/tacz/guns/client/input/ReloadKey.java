package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import cn.sh1rocu.tacz.api.event.PlayerTickEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ReloadKey {
    public static final KeyMapping RELOAD_KEY = new KeyMapping("key.tacz.reload.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            com.tacz.guns.util.InputExtraCheck.TACZ_CATEGORY);

    public static void onReloadPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && RELOAD_KEY.matches(new net.minecraft.client.input.KeyEvent(event.getKey(), event.getScanCode(), event.getModifiers()))) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return;
            }
            if (player.getMainHandItem().getItem() instanceof IGun iGun) {
                // 如果使用背包直读，且没有换弹冷却机制，则在输入时就屏蔽换弹
                if (iGun.useInventoryAmmo(player.getMainHandItem())) {
                    return;
                }
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
            }
        }
    }

    public static boolean onReloadControllerPress(boolean isPress) {
        if (isInGame() && isPress) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return false;
            }
            if (IGun.mainHandHoldGun(player)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
                return true;
            }
        }
        return false;
    }

    @Environment(EnvType.CLIENT)
    public static void autoReload(PlayerTickEvent.Pre event) {
        if (!event.getEntity().level().isClientSide())
            return;

        if (!KeyConfig.AUTO_RELOAD.get()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.tickCount % 5 != 0) {
            return;
        }
        ItemStack currentGunItem = player.getMainHandItem();
        if (player.getMainHandItem().getItem() instanceof IGun iGun) {
            // 如果使用背包直读，且没有换弹冷却机制，则在输入时就屏蔽换弹
            if (iGun.useInventoryAmmo(player.getMainHandItem())) {
                return;
            }
            boolean flag = TimelessAPI.getCommonGunIndex(iGun.getGunId(currentGunItem))
                    .map(gunIndex -> gunIndex.getGunData().getBolt() != Bolt.OPEN_BOLT)
                    .orElse(false);

            int ammoCount = iGun.getCurrentAmmoCount(currentGunItem) + (iGun.hasBulletInBarrel(currentGunItem) && flag ? 1 : 0);
            if (ammoCount > 0) {
                return;
            }
            IClientPlayerGunOperator.fromLocalPlayer(player).reload();
        }
    }
}
