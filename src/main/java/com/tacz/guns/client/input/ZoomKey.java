package com.tacz.guns.client.input;

import cn.sh1rocu.tacz.api.event.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.network.message.ClientMessagePlayerZoom;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Environment(EnvType.CLIENT)
public class ZoomKey {
    public static final KeyMapping ZOOM_KEY = new KeyMapping("key.tacz.zoom.desc",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.category.tacz");

    public static void onZoomKeyPress(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && ZOOM_KEY.matches(event.getKey(), event.getScanCode())) {
            doZoomLogic();
        }
    }

    public static void onZoomMousePress(InputEvent.MouseButton.Post event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && ZOOM_KEY.matchesMouse(event.getButton())) {
            doZoomLogic();
        }
    }

    public static boolean onZoomControllerPress(boolean isPress) {
        if (isInGame() && isPress) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || player.isSpectator()) {
                return false;
            }
            IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
            if (operator.isAim()) {
                ClientPlayNetworking.send(new ClientMessagePlayerZoom());
                return true;
            }
        }
        return false;
    }

    private static void doZoomLogic() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator()) {
            return;
        }
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(player);
        if (operator.isAim()) {
            ClientPlayNetworking.send(new ClientMessagePlayerZoom());
        }
    }
}
