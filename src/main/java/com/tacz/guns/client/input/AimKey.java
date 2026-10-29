package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.config.client.KeyConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class AimKey {
    public static final KeyMapping AIM_KEY = new KeyMapping("key.tacz.aim.desc",
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_RIGHT,
            "key.category.tacz");
    private static boolean controllerAimDown;

    public static boolean onAimPress() {
        return onAimInput(true);
    }

    /**
     * 合并键鼠和手柄的按住状态，同时保留切换武器时的瞄准预输入。
     */
    public static void onAimHoldingPreInput(Minecraft client) {
        if (/*event.phase != TickEvent.Phase.END ||*/ !KeyConfig.HOLD_TO_AIM.get()) {
            return;
        }
        onAimInput(false);
    }

    public static boolean onAimControllerPress(boolean isPress) {
        controllerAimDown = isPress;
        return onAimInput(isPress);
    }

    public static void setControllerAimDown(boolean isDown) {
        controllerAimDown = isDown;
    }

    private static boolean onAimInput(boolean isPress) {
        if (!isInGame()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        if (!(player instanceof IClientPlayerGunOperator operator)) {
            return false;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return false;
        }
        boolean holdToAim = KeyConfig.HOLD_TO_AIM.get();
        if (!isPress && !holdToAim) {
            return false;
        }
        boolean aim = holdToAim ? AIM_KEY.isDown() || controllerAimDown : !operator.isAim();
        if (operator.isAim() != aim) {
            operator.aim(aim);
        }
        return true;
    }

    public static void cancelAim(Minecraft client) {
//        if (event.phase != TickEvent.Phase.END) {
//            return;
//        }
        Minecraft mc = Minecraft.getInstance();
        if (!isInGame()) {
            controllerAimDown = false;
        }
        LocalPlayer player = mc.player;
        if (!(player instanceof IClientPlayerGunOperator operator)) {
            return;
        }
        if (operator.isAim() && (!isInGame() || player.isSpectator())) {
            IClientPlayerGunOperator.fromLocalPlayer(player).aim(false);
        }
    }
}
