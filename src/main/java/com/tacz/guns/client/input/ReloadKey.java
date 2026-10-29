package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.LogicalSide;
import cn.sh1rocu.tacz.api.event.PlayerTickEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import dev.architectury.event.events.common.TickEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
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
            "key.category.tacz");

    public static boolean onReloadPress() {
        if (isInGame()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return false;
            }
            if (player.getMainHandItem().getItem() instanceof IGun iGun) {
                // 如果使用背包直读，且没有换弹冷却机制，则在输入时就屏蔽换弹
                if (iGun.useInventoryAmmo(player.getMainHandItem())) {
                    return false;
                }
                IClientPlayerGunOperator.fromLocalPlayer(player).reload();
                return true;
            }
        }
        return false;
    }

    public static boolean onReloadControllerPress(boolean isPress) {
        return isPress && onReloadPress();
    }

    @Environment(EnvType.CLIENT)
    public static void autoReload(PlayerTickEvent event) {
        if (/*event.phase != TickEvent.Phase.START ||*/ !event.getEntity().level().isClientSide()) {
            return;
        }

        if (!KeyConfig.AUTO_RELOAD.get()) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.tickCount % 5 != 0) {
            return;
        }
        if (IClientPlayerGunOperator.fromLocalPlayer(player).getDataHolder().autoReloadSuppressed) {
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
