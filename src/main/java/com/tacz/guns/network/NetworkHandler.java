package com.tacz.guns.network;

import cn.sh1rocu.tacz.util.neoforge.network.AdvancedAddEntityPayload;
import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class NetworkHandler {
    private static <T extends CustomPacketPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
    }

    private static <T extends CustomPacketPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
    }

    @Environment(EnvType.CLIENT)
    public static <T extends CustomPacketPayload> void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(AdvancedAddEntityPayload.TYPE, AdvancedAddEntityPayload::handle);

        ClientPlayNetworking.registerGlobalReceiver(ServerMessageAmmoStorageContents.TYPE, ServerMessageAmmoStorageContents::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSound.TYPE, ServerMessageSound::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageBulletHitBlock.TYPE, ServerMessageBulletHitBlock::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageCraft.TYPE, ServerMessageCraft::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageRefreshRefitScreen.TYPE, ServerMessageRefreshRefitScreen::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSwapItem.TYPE, ServerMessageSwapItem::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageLevelUp.TYPE, ServerMessageLevelUp::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunHurt.TYPE, ServerMessageGunHurt::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunKill.TYPE, ServerMessageGunKill::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageUpdateEntityData.TYPE, ServerMessageUpdateEntityData::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSyncGunPack.TYPE, ServerMessageSyncGunPack::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunDraw.TYPE, ServerMessageGunDraw::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFinishReload.TYPE, ServerMessageGunFinishReload::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFire.TYPE, ServerMessageGunFire::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFirePost.TYPE, ServerMessageGunFirePost::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunFireSelect.TYPE, ServerMessageGunFireSelect::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunMelee.TYPE, ServerMessageGunMelee::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunReload.TYPE, ServerMessageGunReload::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunShoot.TYPE, ServerMessageGunShoot::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageSyncBaseTimestamp.TYPE, ServerMessageSyncBaseTimestamp::handle);
        ClientPlayNetworking.registerGlobalReceiver(ServerMessageGunUnload.TYPE, ServerMessageGunUnload::handle);
    }

    public static void registerPackets() {
        registerC2SPackets();
        registerS2CPackets();
    }

    public static void registerC2SPackets() {
        registerC2SPacket(ClientMessagePlayerShoot.TYPE, ClientMessagePlayerShoot.STREAM_CODEC, ClientMessagePlayerShoot::handle);
        registerC2SPacket(ClientMessagePlayerReloadGun.TYPE, ClientMessagePlayerReloadGun.STREAM_CODEC, ClientMessagePlayerReloadGun::handle);
        registerC2SPacket(ClientMessagePlayerCancelReload.TYPE, ClientMessagePlayerCancelReload.STREAM_CODEC, ClientMessagePlayerCancelReload::handle);
        registerC2SPacket(ClientMessagePlayerFireSelect.TYPE, ClientMessagePlayerFireSelect.STREAM_CODEC, ClientMessagePlayerFireSelect::handle);
        registerC2SPacket(ClientMessagePlayerAim.TYPE, ClientMessagePlayerAim.STREAM_CODEC, ClientMessagePlayerAim::handle);
        registerC2SPacket(ClientMessagePlayerCrawl.TYPE, ClientMessagePlayerCrawl.STREAM_CODEC, ClientMessagePlayerCrawl::handle);
        registerC2SPacket(ClientMessagePlayerDrawGun.TYPE, ClientMessagePlayerDrawGun.STREAM_CODEC, ClientMessagePlayerDrawGun::handle);
        registerC2SPacket(ClientMessageCraft.TYPE, ClientMessageCraft.STREAM_CODEC, ClientMessageCraft::handle);
        registerC2SPacket(ClientMessagePlayerZoom.TYPE, ClientMessagePlayerZoom.STREAM_CODEC, ClientMessagePlayerZoom::handle);
        registerC2SPacket(ClientMessageRefitGun.TYPE, ClientMessageRefitGun.STREAM_CODEC, ClientMessageRefitGun::handle);
        registerC2SPacket(ClientMessageInstallVirtualAttachment.TYPE, ClientMessageInstallVirtualAttachment.STREAM_CODEC, ClientMessageInstallVirtualAttachment::handle);
        registerC2SPacket(ClientMessageUnloadAttachment.TYPE, ClientMessageUnloadAttachment.STREAM_CODEC, ClientMessageUnloadAttachment::handle);
        registerC2SPacket(ClientMessagePlayerBoltGun.TYPE, ClientMessagePlayerBoltGun.STREAM_CODEC, ClientMessagePlayerBoltGun::handle);
        registerC2SPacket(ClientMessagePlayerMelee.TYPE, ClientMessagePlayerMelee.STREAM_CODEC, ClientMessagePlayerMelee::handle);
        registerC2SPacket(ClientMessageSyncBaseTimestamp.TYPE, ClientMessageSyncBaseTimestamp.STREAM_CODEC, ClientMessageSyncBaseTimestamp::handle);
        registerC2SPacket(ClientMessageLaserColor.TYPE, ClientMessageLaserColor.STREAM_CODEC, ClientMessageLaserColor::handle);
        registerC2SPacket(ClientMessageAmmoStorageInteract.TYPE, ClientMessageAmmoStorageInteract.STREAM_CODEC, ClientMessageAmmoStorageInteract::handle);
        registerC2SPacket(ClientMessageAmmoStorageClick.TYPE, ClientMessageAmmoStorageClick.STREAM_CODEC, ClientMessageAmmoStorageClick::handle);
        registerC2SPacket(ClientMessageSetAttachmentVariant.TYPE, ClientMessageSetAttachmentVariant.STREAM_CODEC, ClientMessageSetAttachmentVariant::handle);
        registerC2SPacket(ClientMessageSetSlotAdapter.TYPE, ClientMessageSetSlotAdapter.STREAM_CODEC, ClientMessageSetSlotAdapter::handle);
        registerC2SPacket(ClientMessagePlayerUnloadGun.TYPE, ClientMessagePlayerUnloadGun.STREAM_CODEC, ClientMessagePlayerUnloadGun::handle);
    }

    public static void registerS2CPackets() {
        registerS2CPacket(AdvancedAddEntityPayload.TYPE, AdvancedAddEntityPayload.STREAM_CODEC);

        registerS2CPacket(ServerMessageAmmoStorageContents.TYPE, ServerMessageAmmoStorageContents.STREAM_CODEC);
        registerS2CPacket(ServerMessageSound.TYPE, ServerMessageSound.STREAM_CODEC);
        registerS2CPacket(ServerMessageBulletHitBlock.TYPE, ServerMessageBulletHitBlock.STREAM_CODEC);
        registerS2CPacket(ServerMessageCraft.TYPE, ServerMessageCraft.STREAM_CODEC);
        registerS2CPacket(ServerMessageRefreshRefitScreen.TYPE, ServerMessageRefreshRefitScreen.STREAM_CODEC);
        registerS2CPacket(ServerMessageSwapItem.TYPE, ServerMessageSwapItem.STREAM_CODEC);
        registerS2CPacket(ServerMessageLevelUp.TYPE, ServerMessageLevelUp.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunHurt.TYPE, ServerMessageGunHurt.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunKill.TYPE, ServerMessageGunKill.STREAM_CODEC);
        registerS2CPacket(ServerMessageUpdateEntityData.TYPE, ServerMessageUpdateEntityData.STREAM_CODEC);
        registerS2CPacket(ServerMessageSyncGunPack.TYPE, ServerMessageSyncGunPack.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunDraw.TYPE, ServerMessageGunDraw.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunFinishReload.TYPE, ServerMessageGunFinishReload.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunFire.TYPE, ServerMessageGunFire.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunFirePost.TYPE, ServerMessageGunFirePost.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunFireSelect.TYPE, ServerMessageGunFireSelect.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunMelee.TYPE, ServerMessageGunMelee.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunReload.TYPE, ServerMessageGunReload.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunShoot.TYPE, ServerMessageGunShoot.STREAM_CODEC);
        registerS2CPacket(ServerMessageSyncBaseTimestamp.TYPE, ServerMessageSyncBaseTimestamp.STREAM_CODEC);
        registerS2CPacket(ServerMessageGunUnload.TYPE, ServerMessageGunUnload.STREAM_CODEC);
    }

    public static void sendToClientPlayer(CustomPacketPayload message, ServerPlayer player) {
        ServerPlayNetworking.send(player, message);
    }

    /**
     * 发送给所有监听此实体的玩家
     */
    public static void sendToTrackingEntityAndSelf(Entity centerEntity, CustomPacketPayload message) {
        if (centerEntity instanceof ServerPlayer player) {
            sendToClientPlayer(message, player);
        }
        sendToTrackingEntity(message, centerEntity);
    }

    public static void sendToAllPlayers(CustomPacketPayload message, MinecraftServer server) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public static void sendToTrackingEntity(CustomPacketPayload message, final Entity centerEntity) {
        for (ServerPlayer player : PlayerLookup.tracking(centerEntity)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public static void sendToDimension(CustomPacketPayload message, final Entity centerEntity) {
        if (centerEntity.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : PlayerLookup.world(serverLevel)) {
                ServerPlayNetworking.send(player, message);
            }
        }
    }
}
