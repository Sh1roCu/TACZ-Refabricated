package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import com.tacz.guns.config.sync.SyncConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class CrawlKey {
    public static final KeyMapping CRAWL_KEY = new KeyMapping("key.tacz.crawl.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            com.tacz.guns.util.InputExtraCheck.TACZ_CATEGORY);

    public static void onCrawlPress(InputEvent.Key event) {
        if (isInGame() && CRAWL_KEY.matches(new net.minecraft.client.input.KeyEvent(event.getKey(), event.getScanCode(), event.getModifiers()))) {
            if (!SyncConfig.ENABLE_CRAWL.get()) {
                return;
            }
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator() || player.isPassenger()) {
                return;
            }
            if (!(player instanceof IClientPlayerGunOperator operator)) {
                return;
            }
            if (player.getMainHandItem().getItem() instanceof IGun iGun) {
                // 如果不允许下蹲，则禁止进行下蹲
                if (!iGun.isCanCrawl(player.getMainHandItem())) {
                    IClientPlayerGunOperator.fromLocalPlayer(player).crawl(false);
                    return;
                }
                boolean action = true;
                if (!KeyConfig.HOLD_TO_CRAWL.get()) {
                    action = !operator.isCrawl();
                }
                if (event.getAction() == GLFW.GLFW_PRESS) {
                    IClientPlayerGunOperator.fromLocalPlayer(player).crawl(action);
                }
                if (KeyConfig.HOLD_TO_CRAWL.get() && event.getAction() == GLFW.GLFW_RELEASE) {
                    IClientPlayerGunOperator.fromLocalPlayer(player).crawl(false);
                }
            }
        }
    }

    public static boolean onCrawlControllerPress(boolean isPress) {
        if (!isInGame()) {
            return false;
        }
        if (!SyncConfig.ENABLE_CRAWL.get()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.isPassenger()) {
            return false;
        }
        if (!(player instanceof IClientPlayerGunOperator operator)) {
            return false;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return false;
        }
        boolean action = true;
        if (!KeyConfig.HOLD_TO_CRAWL.get()) {
            action = !operator.isCrawl();
        }
        if (isPress) {
            IClientPlayerGunOperator.fromLocalPlayer(player).crawl(action);
            return true;
        }
        if (KeyConfig.HOLD_TO_CRAWL.get()) {
            IClientPlayerGunOperator.fromLocalPlayer(player).crawl(false);
            return true;
        }
        return false;
    }
}
