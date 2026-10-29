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
public class UnloadKey {
    public static final KeyMapping UNLOAD_KEY = new KeyMapping("key.tacz.unload.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.category.tacz");

    public static void onUnloadPress() {
        if (!isInGame()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.isAlive() && !player.isSpectator()) {
            IClientPlayerGunOperator.fromLocalPlayer(player).unloadAmmo();
        }
    }
}
