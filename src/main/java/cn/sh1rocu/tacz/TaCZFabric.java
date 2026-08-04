package cn.sh1rocu.tacz;

import cn.sh1rocu.tacz.api.event.*;
import cn.sh1rocu.tacz.util.forge.EnumArgument;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.config.ClientConfig;
import com.tacz.guns.config.CommonConfig;
import com.tacz.guns.config.PreLoadConfig;
import com.tacz.guns.config.ServerConfig;
import com.tacz.guns.crafting.NBTIngredient;
import com.tacz.guns.event.*;
import com.tacz.guns.event.ammo.BellRing;
import com.tacz.guns.event.ammo.DestroyGlassBlock;
import com.tacz.guns.init.CapabilityRegistry;
import com.tacz.guns.init.CommandRegistry;
import com.tacz.guns.init.CommonRegistry;
import com.tacz.guns.init.CompatRegistry;
import com.tacz.guns.network.HandshakeNetworking;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.handshake.AcknowledgeC2SPacket;
import com.tacz.guns.network.message.handshake.SyncedEntityDataMappingS2CPacket;
import com.tacz.guns.resource.CommonAssetsManager;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.config.ModConfig;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class TaCZFabric implements ModInitializer {
    public static final ResourceLocation HIGHEST = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "event_highest_priority");
    public static final ResourceLocation HIGH = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "event_high_priority");
    public static final ResourceLocation LOW = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "event_low_priority");
    public static final ResourceLocation LOWEST = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "event_lowest_priority");

    public static final HolderLookup.Provider VANILLA_ACCESS = VanillaRegistries.createLookup();

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

        NeoForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.COMMON, CommonConfig.init());
        NeoForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.SERVER, ServerConfig.init());
        NeoForgeConfigRegistry.INSTANCE.register(GunMod.MOD_ID, ModConfig.Type.CLIENT, ClientConfig.init());

        PayloadTypeRegistry.configurationS2C().register(SyncedEntityDataMappingS2CPacket.TYPE, SyncedEntityDataMappingS2CPacket.STREAM_CODEC);
        PayloadTypeRegistry.configurationC2S().register(AcknowledgeC2SPacket.TYPE, AcknowledgeC2SPacket.STREAM_CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(AcknowledgeC2SPacket.TYPE, AcknowledgeC2SPacket::handle);
        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, SyncedEntityDataMappingS2CPacket.TYPE)) {
                handler.addTask(new HandshakeNetworking.SyncedEntityDataTask(handler));
            }
        });
        NetworkHandler.registerPackets();

        GunMod.setup();
        CommandRegistry.onServerStaring();
        CompatRegistry.onEnqueue();
        ArgumentTypeRegistry.registerArgumentType(ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "enum_argument"), EnumArgument.class,
                new EnumArgument.Info());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            CommonLoadPack.loadGunPack();
        }

        CustomIngredientSerializer.register(NBTIngredient.Serializer.INSTANCE);

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

        NeoForgeModConfigEvents.loading(GunMod.MOD_ID).register(LoadingConfigEvent::onLoadingConfig);
        NeoForgeModConfigEvents.reloading(GunMod.MOD_ID).register(LoadingConfigEvent::onReloadingConfig);

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
    }
}
