package com.tacz.guns.client.input;

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
            "key.category.tacz");

    public static boolean onFireSelectControllerPress(boolean isPress) {
        return isPress && onFireSelectPress();
    }

    public static boolean onFireSelectPress() {
        if (!isInGame()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        if (IGun.mainHandHoldGun(player)) {
            IClientPlayerGunOperator.fromLocalPlayer(player).fireSelect();
            return true;
        }
        return false;
    }
}
