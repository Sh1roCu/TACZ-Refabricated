package cn.sh1rocu.tacz.client;

import cn.sh1rocu.simplebedrockmodel.api.event.RenderTickEvent;
import cn.sh1rocu.simplebedrockmodel.api.event.ViewportEvent;
import cn.sh1rocu.tacz.api.event.*;
import cn.sh1rocu.tacz.api.extension.IItem;
import com.tacz.guns.api.client.event.BeforeRenderHandEvent;
import com.tacz.guns.api.client.event.RenderItemInHandBobEvent;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.client.animation.screen.RefitTransform;
import com.tacz.guns.client.event.*;
import com.tacz.guns.client.gui.overlay.DisplayCaseLockHintOverlay;
import com.tacz.guns.client.init.*;
import com.tacz.guns.client.input.AimKey;
import com.tacz.guns.client.input.GunInputHandler;
import com.tacz.guns.client.input.ReloadKey;
import com.tacz.guns.client.input.ShootKey;
import com.tacz.guns.client.renderer.item.ScreenItemVboContext;
import com.tacz.guns.client.renderer.scope.ScopeThermalRenderer;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.init.CommonRegistry;
import com.tacz.guns.network.NetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.phys.BlockHitResult;

import static cn.sh1rocu.tacz.TaCZFabric.*;

public class TaCZFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        NetworkHandler.registerS2CPackets();
        ClientSetupEvent.init();
        ModContainerScreen.registerScreens();
        ModEntitiesRender.registerEntityRenderers();
        ParticleFactories.registerParticles();
        BuiltInRegistries.ITEM.stream().filter(item -> item instanceof IItem).forEach(clientEx ->
                BuiltinItemRendererRegistry.INSTANCE.register(clientEx, ((IItem) clientEx).getCustomRenderer()));
        GunPegboardColors.setup();
        GunPegboardColors.itemColors();
        GunPegboardColors.blockColors();

        subscribeEvents();
    }

    private void subscribeEvents() {
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> CommonRegistry.onLoadComplete());

        RenderTickEvent.EVENT.register(RefitTransform::tickInterpolation);

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

        ClientPlayConnectionEvents.JOIN.register(CommonNetworkCacheEvent::onClientPlayerLoggingIn);

        // RenderHandEvent.EVENT.register(FirstPersonRenderEvent::onRenderHand);

        RenderItemInHandBobEvent.VIEW.register(FirstPersonRenderGunEvent::cancelItemInHandViewBobbing);
        GunFireEvent.CALLBACK.register(FirstPersonRenderGunEvent::onGunFire);

        ClientTickEvents.START_CLIENT_TICK.register(client -> InventoryEvent.onPlayerChangeSelect(client, false));
        ClientTickEvents.END_CLIENT_TICK.register(client -> InventoryEvent.onPlayerChangeSelect(client, true));
        SwapItemWithOffHand.CALLBACK.register(InventoryEvent::onPlayerSwapMainHand);
        ClientPlayConnectionEvents.DISCONNECT.register(InventoryEvent::onPlayerLoggedOut);

        PlayerEvent.LOGGED_IN.register(PlayerEnterWorld::onPlayerEnterWorld);

        EntityHurtByGunEvent.POST.register(PlayerHurtByGunEvent::onPlayerHurtByGun);

        ClientPlayerNetworkEvent.CLONE.register(RefreshClonePlayerDataEvent::onClientPlayerClone);
        ClientTickEvents.START_CLIENT_TICK.register(RefreshClonePlayerDataEvent::onClientTick);

        TextureStitchEvent.POST.register(ReloadResourceEvent::onTextureStitchEventPost);

        RenderTickEvent.EVENT.register(RenderCrosshairEvent::onRenderTick);

        RenderLivingEvent.POST.register(RenderHeadShotAABB::onRenderEntity);

        ClientTickEvents.START_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);
        ClientTickEvents.END_CLIENT_TICK.register(TickAnimationEvent::tickAnimation);
        RenderTickEvent.EVENT.register(TickAnimationEvent::tickAnimation);

        ItemTooltipCallback.EVENT.register(TooltipEvent::onTooltip);

        ClientTickEvents.END_CLIENT_TICK.register(AimKey::cancelAim);
        ClientTickEvents.START_CLIENT_TICK.register(AimKey::onAimHoldingPreInput);
        ClientTickEvents.END_CLIENT_TICK.register(AimKey::onAimHoldingPreInput);

        PlayerTickEvent.START.register(ReloadKey::autoReload);

        ClientTickEvents.START_CLIENT_TICK.register(mc -> ShootKey.autoShoot(mc, false));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> ShootKey.autoShoot(mc, true));

        InputEvent.Key.EVENT.register(GunInputHandler::onKeyPress);
        InputEvent.MouseButton.Post.EVENT.register(GunInputHandler::onMousePress);
        InputEvent.MouseButton.Pre.EVENT.register(GunInputHandler::onMousePressPre);

        ClientTickEvents.END_CLIENT_TICK.register(SoundPlayManager::onClientTick);

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hitResult) -> {
            if (hitResult instanceof BlockHitResult blockHitResult) {
                return GunPegboardHighlightEvent.onRenderBlockHighlight(ctx, blockHitResult);
            }
            return true;
        });

        ClientTickEvents.END_CLIENT_TICK.register(DisplayCaseLockHintOverlay::onClientTick);


        ClientPlayConnectionEvents.DISCONNECT.addPhaseOrdering(HIGHEST, HIGH);
        ClientPlayConnectionEvents.DISCONNECT.addPhaseOrdering(HIGH, Event.DEFAULT_PHASE);
        ClientPlayConnectionEvents.DISCONNECT.addPhaseOrdering(Event.DEFAULT_PHASE, LOW);
        ClientPlayConnectionEvents.DISCONNECT.addPhaseOrdering(LOW, LOWEST);
        ClientPlayConnectionEvents.DISCONNECT.register(LOWEST, ClientGunTimeResetEvent::onLoggingOut);

        WorldRenderEvents.AFTER_TRANSLUCENT.register(ScopeThermalRenderer::onWorldStage);

        InputEvent.Key.EVENT.register(GunDisplayCaseClientInteractionEvent::onUseKey);
        InputEvent.MouseButton.Post.EVENT.register(GunDisplayCaseClientInteractionEvent::onUseMouse);

        WorldRenderEvents.BEFORE_BLOCK_OUTLINE.register((ctx, hitResult) -> {
            if (hitResult instanceof BlockHitResult blockHitResult) {
                return GunDisplayCaseHighlightEvent.onRenderBlockHighlight(ctx, blockHitResult);
            }
            return true;
        });

        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            var event = ScreenEvents.beforeRender(screen);
            event.addPhaseOrdering(HIGHEST, HIGH);
            event.addPhaseOrdering(HIGH, Event.DEFAULT_PHASE);
            event.addPhaseOrdering(Event.DEFAULT_PHASE, LOW);
            event.addPhaseOrdering(LOW, LOWEST);
            event.register(HIGHEST, ScreenItemVboContext::beforeScreen);
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            var event = ScreenEvents.afterRender(screen);
            event.addPhaseOrdering(HIGHEST, HIGH);
            event.addPhaseOrdering(HIGH, Event.DEFAULT_PHASE);
            event.addPhaseOrdering(Event.DEFAULT_PHASE, LOW);
            event.addPhaseOrdering(LOW, LOWEST);
            event.register(LOWEST, ScreenItemVboContext::afterScreen);
        });
    }
}
