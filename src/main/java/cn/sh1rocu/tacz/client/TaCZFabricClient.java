package cn.sh1rocu.tacz.client;

import cn.sh1rocu.tacz.api.event.*;
// TODO: Custom item renderer imports removed - rendering API changed in 1.21.11
// import cn.sh1rocu.tacz.api.extension.IItem;
// import com.tacz.guns.client.renderer.item.AmmoItemRenderer;
// import com.tacz.guns.client.renderer.item.AnimateGeoItemRenderer;
// import com.tacz.guns.client.renderer.item.AttachmentItemRenderer;
// import com.tacz.guns.client.renderer.item.GunSmithTableItemRenderer;
import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.event.*;
import com.tacz.guns.client.init.ClientSetupEvent;
import com.tacz.guns.client.init.ModContainerScreen;
import com.tacz.guns.client.init.ModEntitiesRender;
import com.tacz.guns.client.init.ParticleFactories;
import com.tacz.guns.client.input.*;
import com.tacz.guns.init.CommonRegistry;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.handshake.SyncedEntityDataMappingS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
// import net.fabricmc.fabric.api.client.rendering.v1.SpecialBlockRendererRegistry;
// import net.minecraft.core.registries.BuiltInRegistries;

public class TaCZFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfigurationNetworking.registerGlobalReceiver(SyncedEntityDataMappingS2CPacket.TYPE, SyncedEntityDataMappingS2CPacket::handle);
        NetworkHandler.registerClientReceivers();

        ClientSetupEvent.init();
        ModContainerScreen.registerScreens();
        ModEntitiesRender.registerEntityRenderers();
        ParticleFactories.registerParticles();
        // TODO: Fabric rendering API completely changed in 1.21.11 - SpecialBlockRendererRegistry now takes (Block, SpecialModelRenderer.Unbaked)
        // The old DynamicItemRenderer pattern is no longer supported. Custom item renderers need to be reimplemented
        // using the new SpecialModelRenderer/SpecialGuiElementRegistry API.
        // BuiltInRegistries.ITEM.stream().filter(item -> item instanceof IItem).forEach(clientEx -> {
        //         Object renderer = ((IItem) clientEx).getCustomRenderer();
        //         ... register custom renderers ...
        // });
        subscribeEvents();
    }

    private void subscribeEvents() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CommonRegistry.onLoadComplete());

        RenderTickEvent.CALLBACK.register(RefitTransform::tickInterpolation);

        ViewportEvent.CAMERA.register(CameraSetupEvent::applyLevelCameraAnimation);
        BeforeRenderHandEvent.CALLBACK.register(CameraSetupEvent::applyItemInHandCameraAnimation);
        ViewportEvent.FOV.register(CameraSetupEvent::applyScopeMagnification);
        ViewportEvent.FOV.register(CameraSetupEvent::applyGunModelFovModifying);
        GunFireEvent.CALLBACK.register(CameraSetupEvent::initialCameraRecoil);
        ViewportEvent.CAMERA.register(CameraSetupEvent::applyCameraRecoil);
        ComputeFovModifierEvent.CALLBACK.register(CameraSetupEvent::onComputeMovementFov);

        EntityHurtByGunEvent.POST.register(ClientHitMark::onEntityHurt);
        EntityKillByGunEvent.CALLBACK.register(ClientHitMark::onEntityKill);

        InputEvent.InteractionKeyMappingTriggered.EVENT.register(ClientPreventGunClick::onClickInput);

        RenderHandEvent.CALLBACK.register(FirstPersonRenderEvent::onRenderHand);

        RenderItemInHandBobEvent.VIEW.register(FirstPersonRenderGunEvent::cancelItemInHandViewBobbing);
        GunFireEvent.CALLBACK.register(FirstPersonRenderGunEvent::onGunFire);

        ClientTickEvents.START_CLIENT_TICK.register(InventoryEvent::onPlayerChangeSelect);
        ClientTickEvents.END_CLIENT_TICK.register(InventoryEvent::onPlayerChangeSelect);
        SwapItemWithOffHand.CALLBACK.register(InventoryEvent::onPlayerSwapMainHand);
        ClientPlayerNetworkEvent.LOGGING_OUT.register(InventoryEvent::onPlayerLoggedOut);

        PlayerEvent.LOGGED_IN.register(PlayerEnterWorld::onPlayerEnterWorld);

        EntityHurtByGunEvent.POST.register(PlayerHurtByGunEvent::onPlayerHurtByGun);

        PlaySoundSourceEvent.CALLBACK.register(PlayGunSoundEvent::onPlaySoundSource);

        ClientPlayerNetworkEvent.CLONE.register(RefreshClonePlayerDataEvent::onClientPlayerClone);
        ClientTickEvents.START_CLIENT_TICK.register(RefreshClonePlayerDataEvent::onClientTick);

        TextureStitchEvent.POST.register(ReloadResourceEvent::onTextureStitchEventPost);

        RenderTickEvent.CALLBACK.register(RenderCrosshairEvent::onRenderTick);

        RenderLivingEvent.POST.register(RenderHeadShotAABB::onRenderEntity);

        ClientTickEvents.START_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);
        ClientTickEvents.END_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);
        RenderTickEvent.CALLBACK.register(TickAnimationEvent::tickAnimation);

        ItemTooltipCallback.EVENT.register(TooltipEvent::onTooltip);

        InputEvent.MouseButton.Post.EVENT.register(AimKey::onAimPress);
        ClientTickEvents.END_CLIENT_TICK.register(AimKey::cancelAim);
        ClientTickEvents.START_CLIENT_TICK.register(AimKey::onAimHoldingPreInput);
        ClientTickEvents.END_CLIENT_TICK.register(AimKey::onAimHoldingPreInput);


        InputEvent.Key.EVENT.register(ConfigKey::onOpenConfig);
        InputEvent.Key.EVENT.register(CrawlKey::onCrawlPress);

        InputEvent.Key.EVENT.register(FireSelectKey::onFireSelectKeyPress);
        InputEvent.MouseButton.Post.EVENT.register(FireSelectKey::onFireSelectMousePress);

        InputEvent.Key.EVENT.register(InspectKey::onInspectPress);

        InputEvent.Key.EVENT.register(InteractKey::onInteractKeyPress);
        InputEvent.MouseButton.Post.EVENT.register(InteractKey::onInteractMousePress);

        InputEvent.Key.EVENT.register(MeleeKey::onMeleeKeyPress);
        InputEvent.MouseButton.Post.EVENT.register(MeleeKey::onMeleeMousePress);

        InputEvent.Key.EVENT.register(RefitKey::onRefitPress);

        InputEvent.Key.EVENT.register(ReloadKey::onReloadPress);
        PlayerTickEvent.START.register(ReloadKey::autoReload);

        ClientTickEvents.START_CLIENT_TICK.register(mc -> ShootKey.autoShoot(mc, false));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> ShootKey.autoShoot(mc, true));
        InputEvent.MouseButton.Post.EVENT.register(ShootKey::semiShoot);

        InputEvent.Key.EVENT.register(ZoomKey::onZoomKeyPress);
        InputEvent.MouseButton.Post.EVENT.register(ZoomKey::onZoomMousePress);
    }
}
