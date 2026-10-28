package com.tacz.guns.network;

import cn.sh1rocu.tacz.api.extension.IEntityAdditionalSpawnData;
import com.tacz.guns.network.message.*;
import com.tacz.guns.network.message.event.*;
import com.tacz.guns.network.message.handshake.AcknowledgeC2SPacket;
import com.tacz.guns.network.message.handshake.SyncedEntityDataMappingS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.fabric.mixin.networking.client.accessor.ClientLoginNetworkHandlerAccessor;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class NetworkHandler {
    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerShoot.TYPE, ClientMessagePlayerShoot::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerReloadGun.TYPE, ClientMessagePlayerReloadGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerCancelReload.TYPE, ClientMessagePlayerCancelReload::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerFireSelect.TYPE, ClientMessagePlayerFireSelect::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerAim.TYPE, ClientMessagePlayerAim::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerCrawl.TYPE, ClientMessagePlayerCrawl::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerDrawGun.TYPE, ClientMessagePlayerDrawGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageCraft.TYPE, ClientMessageCraft::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerZoom.TYPE, ClientMessagePlayerZoom::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageRefitGun.TYPE, ClientMessageRefitGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageInstallVirtualAttachment.TYPE, ClientMessageInstallVirtualAttachment::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageUnloadAttachment.TYPE, ClientMessageUnloadAttachment::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerBoltGun.TYPE, ClientMessagePlayerBoltGun::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerMelee.TYPE, ClientMessagePlayerMelee::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageSyncBaseTimestamp.TYPE, ClientMessageSyncBaseTimestamp::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageLaserColor.TYPE, ClientMessageLaserColor::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageAmmoStorageInteract.TYPE, ClientMessageAmmoStorageInteract::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageAmmoStorageClick.TYPE, ClientMessageAmmoStorageClick::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageSetAttachmentVariant.TYPE, ClientMessageSetAttachmentVariant::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessageSetSlotAdapter.TYPE, ClientMessageSetSlotAdapter::handle);
        ServerPlayNetworking.registerGlobalReceiver(ClientMessagePlayerUnloadGun.TYPE, ClientMessagePlayerUnloadGun::handle);

        HandshakeNetworking.register(AcknowledgeC2SPacket.ID, AcknowledgeC2SPacket.class);
        HandshakeNetworking.register(SyncedEntityDataMappingS2CPacket.TYPE, SyncedEntityDataMappingS2CPacket.class);
    }

    @Environment(EnvType.CLIENT)
    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(IEntityAdditionalSpawnData.EXTRA_DATA_PACKET, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            buf.retain();
            client.execute(() -> {
                Entity entity = Objects.requireNonNull(client.level).getEntity(entityId);
                if (entity instanceof IEntityAdditionalSpawnData extra) {
                    extra.readSpawnData(buf);
                }
                buf.release();
            });
        });

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

    @SuppressWarnings("UnstableApiUsage")
    @Environment(EnvType.CLIENT)
    static <T extends IHandshakeMessage> void registerHandshake(PacketType<T> type) {
        ClientLoginNetworking.registerGlobalReceiver(type.getId(), (client, handler, buf, listenerAdder) -> {
            T packet = type.read(buf);
            Connection connection = ((ClientLoginNetworkHandlerAccessor) handler).getConnection();
            IHandshakeMessage.IResponsePacket responsePacket = packet.handle(connection, listenerAdder);
            FriendlyByteBuf response = PacketByteBufs.create();
            if (responsePacket != null) {
                response.writeResourceLocation(responsePacket.getId());
                responsePacket.write(response);
            }
            return CompletableFuture.completedFuture(response);
        });
    }

    public static void sendToClientPlayer(FabricPacket message, ServerPlayer player) {
        ServerPlayNetworking.send(player, message);
    }

    /**
     * 发送给所有监听此实体的玩家
     */
    public static void sendToTrackingEntityAndSelf(Entity centerEntity, FabricPacket message) {
        if (centerEntity instanceof ServerPlayer player) {
            sendToClientPlayer(message, player);
        }
        sendToTrackingEntity(message, centerEntity);
    }

    public static void sendToAllPlayers(FabricPacket message, MinecraftServer server) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public static void sendToTrackingEntity(FabricPacket message, final Entity centerEntity) {
        for (ServerPlayer player : PlayerLookup.tracking(centerEntity)) {
            ServerPlayNetworking.send(player, message);
        }
    }

    public static void sendToDimension(FabricPacket message, final Entity centerEntity) {
        if (centerEntity.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : PlayerLookup.world(serverLevel)) {
                ServerPlayNetworking.send(player, message);
            }
        }
    }
}
