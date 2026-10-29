package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.ClientPlayerNetworkEvent;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.client.model.functional.MuzzleFlashRender;
import com.tacz.guns.client.particle.MuzzleParticleManager;
import com.tacz.guns.client.particle.ThirdPersonMuzzleParticleManager;
import com.tacz.guns.client.renderer.other.GunHurtBobTweak;
import com.tacz.guns.client.renderer.scope.ScopeLensRenderer;
import com.tacz.guns.client.resource.pojo.display.gun.RangefinderManager;
import com.tacz.guns.util.time.GunTime;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

/**
 * Clears timestamps tied to a client world after SBM resets its animation clock.
 */
@Environment(EnvType.CLIENT)
public final class ClientGunTimeResetEvent {
    private ClientGunTimeResetEvent() {
    }

    public static void onLoggingOut(ClientPacketListener handler, Minecraft client) {
        ScopeLensRenderer.close();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            LocalPlayerDataHolder dataHolder = IClientPlayerGunOperator.fromLocalPlayer(player).getDataHolder();
            dataHolder.cancelShootFutures();
            dataHolder.resetAmmoCountCache();
        }
        GunTime.resetEpoch();
        LocalPlayerDataHolder.clientClickButtonTimestamp = -1L;
        RefitTransform.resetTimestamps();
        RenderCrosshairEvent.resetTimestamps();
        KillAmountOverlay.resetTimestamp();
        ClientHitMark.resetTimestamp();
        GunHudOverlay.resetTimestamp();
        FirstPersonRenderGunEvent.resetTimestamps();
        CameraSetupEvent.resetTimestamp();
        MuzzleFlashRender.resetTimestamps();
        MuzzleParticleManager.resetMuzzleLight();
        ThirdPersonMuzzleParticleManager.resetTimestamps();
        RangefinderManager.resetTimestamps();
        GunHurtBobTweak.resetTimestamp();
    }
}
