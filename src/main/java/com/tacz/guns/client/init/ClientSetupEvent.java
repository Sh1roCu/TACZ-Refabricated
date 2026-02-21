package com.tacz.guns.client.init;

import com.tacz.guns.api.client.other.ThirdPersonManager;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import com.tacz.guns.client.gui.overlay.HeatBarOverlay;
import com.tacz.guns.client.gui.overlay.InteractKeyTextOverlay;
import com.tacz.guns.client.gui.overlay.KillAmountOverlay;
import com.tacz.guns.client.input.*;
import com.tacz.guns.client.resource.ClientAssetsManager;
import com.tacz.guns.client.tooltip.ClientAmmoBoxTooltip;
import com.tacz.guns.client.tooltip.ClientAttachmentItemTooltip;
import com.tacz.guns.client.tooltip.ClientBlockItemTooltip;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.inventory.tooltip.AmmoBoxTooltip;
import com.tacz.guns.inventory.tooltip.AttachmentItemTooltip;
import com.tacz.guns.inventory.tooltip.BlockItemTooltip;
import com.tacz.guns.inventory.tooltip.GunTooltip;
import com.tacz.guns.item.AmmoBoxItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.server.packs.PackType;

@Environment(EnvType.CLIENT)
public class ClientSetupEvent {
    public static void init() {
        registerKeyMappings();
        registerClientTooltips();
        registerGuiOverlays();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> onClientSetup());
        onClientResourceReload();
    }

    public static void registerKeyMappings() {
        // 注册键位
        // MKB 1.21存在不能单独设置alt, ctrl, shift的bug，暂时弃用
        registerKeyBinding(InspectKey.INSPECT_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(ReloadKey.RELOAD_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(ShootKey.SHOOT_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(InteractKey.INTERACT_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(FireSelectKey.FIRE_SELECT_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(AimKey.AIM_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(CrawlKey.CRAWL_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(RefitKey.REFIT_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(ZoomKey.ZOOM_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(MeleeKey.MELEE_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.NONE*/);
        registerKeyBinding(ConfigKey.OPEN_CONFIG_KEY/*, KeyConflictContext.IN_GAME, KeyModifier.ALT*/);
    }

    private static void registerKeyBinding(KeyMapping keyMapping/*, KeyConflictContext keyConflictContext, KeyModifier keyModifier*/) {
        KeyBindingHelper.registerKeyBinding(keyMapping);
//        var iKey = (IKeyBinding) keyMapping;
//        iKey.setKeyConflictContext(keyConflictContext);
//        iKey.setKeyModifierAndCode(keyModifier, iKey.getKey());
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
        HudRenderCallback.EVENT.register(InteractKeyTextOverlay.INSTANCE::render);
        HudRenderCallback.EVENT.register(KillAmountOverlay.INSTANCE::render);
    }

    public static void onClientSetup() {
        // 注册自己的的硬编码第三人称动画
        ThirdPersonManager.registerDefault();

        // TODO: In 1.21.11 ColorProviderRegistry.ITEM was removed (item color providers moved to item model system)
        // ColorProviderRegistry.ITEM.register(AmmoBoxItem::getColor, ModItems.AMMO_BOX);

        // TODO: ItemProperties removed in 1.21.11 - item model system reworked
        // ItemProperties.register(ModItems.AMMO_BOX, AmmoBoxItem.PROPERTY_NAME, AmmoBoxItem::getStatue);

        // 初始化自己的枪包下载器
//       ClientGunPackDownloadManager.init();

//        // 与 player animator 的兼容
//       PlayerAnimatorCompat.init();

        // TODO: Compat modules disabled - excluded from compilation until dependencies have 1.21.11 builds
        // ShoulderSurfingCompat.init();
        // ControllableCompat.init();
        // ARCompat.init();
        // ZoomifyCompat.init();
        // ImmediatelyFastCompat.init();
    }

    public static void onClientResourceReload() {
        PlayerAnimatorCompat.init();

        ClientAssetsManager.INSTANCE.reloadAndRegister(ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)::registerReloadListener);
        if (PlayerAnimatorCompat.isInstalled()) {
            PlayerAnimatorCompat.registerReloadListener(ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)::registerReloadListener);
        }
    }
}
