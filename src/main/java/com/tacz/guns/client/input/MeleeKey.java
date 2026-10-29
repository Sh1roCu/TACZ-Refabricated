package com.tacz.guns.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class MeleeKey {
    public static final KeyMapping MELEE_KEY = new KeyMapping("key.tacz.melee.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.tacz");

    public static boolean onMeleeControllerPress(boolean isPress) {
        return isPress && onMeleePress();
    }

    public static boolean onMeleePress() {
        if (!isInGame()) {
            return false;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return false;
        }
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
        if (!operator.isAim()) {
            operator.melee();
            return true;
        }
        return false;
    }
}
