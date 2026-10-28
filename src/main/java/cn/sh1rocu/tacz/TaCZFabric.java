package cn.sh1rocu.tacz;

import cn.sh1rocu.tacz.api.event.*;
import cn.sh1rocu.tacz.util.forge.EnumArgument;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.config.ClientConfig;
import com.tacz.guns.config.CommonConfig;
import com.tacz.guns.config.PreLoadConfig;
import com.tacz.guns.config.ServerConfig;
import com.tacz.guns.event.*;
import com.tacz.guns.event.ammo.BellRing;
import com.tacz.guns.event.ammo.DestroyGlassBlock;
import com.tacz.guns.init.CapabilityRegistry;
import com.tacz.guns.init.CommandRegistry;
import com.tacz.guns.init.CommonRegistry;
import com.tacz.guns.init.CompatRegistry;
import com.tacz.guns.resource.CommonAssetsManager;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class TaCZFabric implements ModInitializer {
    public static final ResourceLocation HIGHEST = new ResourceLocation(GunMod.MOD_ID, "event_highest_priority");
    public static final ResourceLocation HIGH = new ResourceLocation(GunMod.MOD_ID, "event_high_priority");
    public static final ResourceLocation LOW = new ResourceLocation(GunMod.MOD_ID, "event_low_priority");
    public static final ResourceLocation LOWEST = new ResourceLocation(GunMod.MOD_ID, "event_lowest_priority");

    @Nullable
    private static WeakReference<MinecraftServer> server;

    @Nullable
    public static MinecraftServer getServer() {
        if (server == null) {
            return null;
        }
        return server.get();
    }

    @Override
    public void onInitialize() {
        // 确保配置文件加载，这个阶段将比标准的forge配置文件加载早
        PreLoadConfig.init();

        ForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.COMMON, CommonConfig.init());
        ForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.SERVER, ServerConfig.init());
        ForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.CLIENT, ClientConfig.init());

        GunMod.setup();
        CommandRegistry.onServerStaring();
        CompatRegistry.onEnqueue();
        ArgumentTypeRegistry.registerArgumentType(new ResourceLocation(GunMod.MOD_ID, "enum_argument"), EnumArgument.class,
                new EnumArgument.Info());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            CommonLoadPack.loadGunPack();
        }
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> TaCZFabric.server = new WeakReference<>(server));

        subscribeEvents();
    }

    private void subscribeEvents() {
        CapabilityRegistry.init();

        AddReloadListenerEvent.CALLBACK.register(CommonAssetsManager::onReload);
        CommonLifecycleEvents.TAGS_LOADED.register(CommonAssetsManager::onReload);
        ServerLifecycleEvents.SERVER_STOPPED.register(CommonAssetsManager::onServerStopped);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(CommonAssetsManager::OnDatapackSync);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> CommonRegistry.onLoadComplete());

        AmmoHitBlockEvent.CALLBACK.register(BellRing::onAmmoHitBlock);

        AmmoHitBlockEvent.CALLBACK.register(DestroyGlassBlock::onAmmoHitBlock);

        LivingHurtEvent.CALLBACK.register(LOW, EntityDamageEvent::onLivingHurt);

        PlayerTickEvent.END.register(HitboxHelperEvent::onPlayerTick);
        PlayerEvent.LOGGED_OUT.register(HitboxHelperEvent::onPlayerLoggedOut);

        LivingKnockBackEvent.CALLBACK.register(KnockbackChange::onKnockback);

        ModConfigEvents.loading(GunMod.MOD_ID).register(LoadingConfigEvent::onLoadingConfig);
        ModConfigEvents.reloading(GunMod.MOD_ID).register(LoadingConfigEvent::onReloadingConfig);

        ServerPlayerEvents.AFTER_RESPAWN.register(PlayerRespawnEvent::onPlayerRespawn);

        AttackBlockCallback.EVENT.register(PreventGunClick::onLeftClickBlock);

        ServerTickEvents.START_SERVER_TICK.register(ServerTickEvent::onServerTick);
        ServerTickEvents.END_SERVER_TICK.register(ServerTickEvent::onServerTick);

        EntityJoinLevelEvent.CALLBACK.register(SyncBaseTimestamp::onPlayerJoinWorld);

        EntityTrackingEvents.START_TRACKING.register(SyncedEntityDataEvent::onStartTracking);
        EntityJoinLevelEvent.CALLBACK.register(SyncedEntityDataEvent::onPlayerJoinWorld);
        ServerPlayerEvents.COPY_FROM.register(SyncedEntityDataEvent::onPlayerClone);
        ServerTickEvents.END_SERVER_TICK.register(SyncedEntityDataEvent::onServerTick);

        ServerEntityWorldChangeEvents.AFTER_ENTITY_CHANGE_WORLD.register(TravelToDimensionEvent::onTravelToDimension);
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register(TravelToDimensionEvent::onTravelToDimension);

        AttackBlockCallback.EVENT.register(GunPegboardInteractionEvent::onLeftClickBlock);

        UseBlockCallback.EVENT.addPhaseOrdering(HIGHEST, HIGH);
        UseBlockCallback.EVENT.addPhaseOrdering(HIGH, Event.DEFAULT_PHASE);
        UseBlockCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, LOW);
        UseBlockCallback.EVENT.addPhaseOrdering(LOW, LOWEST);

        UseBlockCallback.EVENT.register(LOW, AmmoStorageInteractionEvent::onRightClickBlock);

        UseBlockCallback.EVENT.register(GunDisplayCaseInteractionEvent::onRightClickBlock);
        AttackBlockCallback.EVENT.register(GunDisplayCaseInteractionEvent::onLeftClickBlock);

        GunFireEvent.CALLBACK.register(LOWEST, GunSoundAngerEvent::onGunFire);
    }
}
