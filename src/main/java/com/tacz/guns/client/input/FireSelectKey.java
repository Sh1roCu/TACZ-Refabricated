package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class FireSelectKey {
    public static final KeyMapping FIRE_SELECT_KEY = new KeyMapping("key.tacz.fire_select.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            com.tacz.guns.util.InputExtraCheck.TACZ_CATEGORY);

    public static void onFireSelectKeyPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && FIRE_SELECT_KEY.matches(new net.minecraft.client.input.KeyEvent(event.getKey(), event.getScanCode(), event.getModifiers()))) {
            doFireSelectLogic();
        }
    }

    public static void onFireSelectMousePress(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && FIRE_SELECT_KEY.matchesMouse(new net.minecraft.client.input.MouseButtonEvent(0, 0, new net.minecraft.client.input.MouseButtonInfo(event.getButton(), event.getModifiers())))) {
            doFireSelectLogic();
        }
    }

    public static boolean onFireSelectControllerPress(boolean isPress) {
        if (isInGame() && isPress) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return false;
            }
            if (IGun.mainHandHoldGun(player)) {
                IClientPlayerGunOperator.fromLocalPlayer(player).fireSelect();
                return true;
            }
        }
        return false;
    }

    private static void doFireSelectLogic() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        if (IGun.mainHandHoldGun(player)) {
            IClientPlayerGunOperator.fromLocalPlayer(player).fireSelect();
        }
    }
}
