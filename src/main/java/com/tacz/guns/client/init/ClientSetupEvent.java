package com.tacz.guns.client.init;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.other.ThirdPersonManager;
import com.tacz.guns.client.gui.overlay.*;
import com.tacz.guns.client.input.GunInputHandler;
import com.tacz.guns.client.renderer.scope.ScopeLensRenderer;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.client.tooltip.*;
import com.tacz.guns.compat.ar.ARCompat;
import com.tacz.guns.compat.controllable.ControllableCompat;
import com.tacz.guns.compat.immediatelyfast.ImmediatelyFastCompat;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.tacz.guns.compat.shouldersurfing.ShoulderSurfingCompat;
import com.tacz.guns.compat.zoomify.ZoomifyCompat;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.inventory.tooltip.*;
import com.tacz.guns.item.AmmoBoxItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Environment(EnvType.CLIENT)
public class ClientSetupEvent {
    public static void init() {
        onRegisterShaders();
        registerKeyMappings();
        registerClientTooltips();
        registerGuiOverlays();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> onClientSetup());
        onClientResourceReload();
    }

    public static void onRegisterShaders() {
        ScopeLensRenderer.registerShader();
    }

    public static void registerKeyMappings() {
        // 注册键位
        GunInputHandler.registerKeyMappings();
    }

    public static void registerClientTooltips() {
        // 注册文本提示
        TooltipComponentCallback.EVENT.register(tooltip -> {
            if (tooltip instanceof GunTooltip gunTooltip) {
                return new ClientGunTooltip(gunTooltip);
            }
            if (tooltip instanceof AmmoBoxTooltip ammoBoxTooltip) {
                return new ClientAmmoBoxTooltip(ammoBoxTooltip);
            }
            if (tooltip instanceof AttachmentItemTooltip attachmentItemTooltip) {
                return new ClientAttachmentItemTooltip(attachmentItemTooltip);
            }
            if (tooltip instanceof AmmoStorageTooltip ammoStorageTooltip) {
                return new ClientAmmoStorageTooltip(ammoStorageTooltip);
            }
            if (tooltip instanceof BlockItemTooltip blockItemTooltip) {
                return new ClientBlockItemTooltip(blockItemTooltip);
            }
            return null;
        });
    }

    public static void registerGuiOverlays() {
        // 注册 HUD
        HudRenderCallback.EVENT.register(GunHudOverlay.INSTANCE::render);
        HudRenderCallback.EVENT.register(HeatBarOverlay.INSTANCE::render);
        HudRenderCallback.EVENT.register(KillAmountOverlay.INSTANCE::render);
        HudRenderCallback.EVENT.register(InteractKeyTextOverlay.INSTANCE::render);
        HudRenderCallback.EVENT.register(DisplayCaseLockHintOverlay.INSTANCE::render);
    }

    public static void onClientSetup() {
        // 注册自己的的硬编码第三人称动画
        ThirdPersonManager.registerDefault();

        // 注册颜色
        ColorProviderRegistry.ITEM.register(AmmoBoxItem::getColor, ModItems.AMMO_BOX);

        // 注册变种
        // noinspection deprecation
        ItemProperties.register(ModItems.AMMO_BOX, AmmoBoxItem.PROPERTY_NAME, AmmoBoxItem::getStatue);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUN_DISPLAY_CASE, RenderType.cutout());

        // 初始化自己的枪包下载器
//       ClientGunPackDownloadManager.init();

//        // 与 player animator 的兼容
//       PlayerAnimatorCompat.init();

        // 与 Shoulder Surfing Reloaded 的兼容
        ShoulderSurfingCompat.init();

        // 与 Controllable 的兼容
        ControllableCompat.init();

        // 与 Accelerated Rendering 的兼容
        ARCompat.init();

        ZoomifyCompat.init();
        ImmediatelyFastCompat.init();
    }

    public static void onClientResourceReload() {
        PlayerAnimatorCompat.init();

        var clientRegistry = ResourceManagerHelper.get(PackType.CLIENT_RESOURCES);
        clientRegistry.registerReloadListener(new IdentifiableResourceReloadListener() {
            static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "sound_manager");

            @Override
            public ResourceLocation getFabricId() {
                return ID;
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller preparationProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return barrier.wait(Void.TYPE).thenRunAsync(SoundPlayManager::clearSoundResourceCache, gameExecutor);
            }
        });
        ClientAssetsManager.INSTANCE.reloadAndRegister(clientRegistry::registerReloadListener);
        if (PlayerAnimatorCompat.isInstalled()) {
            PlayerAnimatorCompat.registerReloadListener(clientRegistry::registerReloadListener);
        }
    }
}
