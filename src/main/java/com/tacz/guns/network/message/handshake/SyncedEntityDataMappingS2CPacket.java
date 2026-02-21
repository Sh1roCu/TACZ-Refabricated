package com.tacz.guns.network.message.handshake;

import com.tacz.guns.GunMod;
import com.tacz.guns.entity.sync.core.SyncedDataKey;
import com.tacz.guns.entity.sync.core.SyncedEntityData;
import com.tacz.guns.network.IHandshakeMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class SyncedEntityDataMappingS2CPacket implements IHandshakeMessage {
    public static final CustomPacketPayload.Type<SyncedEntityDataMappingS2CPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "server_synced_entity_data_mapping")
    );
    public static final StreamCodec<FriendlyByteBuf, SyncedEntityDataMappingS2CPacket> STREAM_CODEC = StreamCodec.of(
            SyncedEntityDataMappingS2CPacket::encode,
            SyncedEntityDataMappingS2CPacket::decode
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static final Marker HANDSHAKE = MarkerFactory.getMarker("TACZ_HANDSHAKE");
    private Map<Identifier, List<Pair<Identifier, Integer>>> keyMap;

    public SyncedEntityDataMappingS2CPacket() {
        this.keyMap = new HashMap<>();
    }

    private SyncedEntityDataMappingS2CPacket(Map<Identifier, List<Pair<Identifier, Integer>>> keyMap) {
        this.keyMap = keyMap;
    }

    public static void encode(FriendlyByteBuf buffer, SyncedEntityDataMappingS2CPacket message) {
        Set<SyncedDataKey<?, ?>> keys = SyncedEntityData.instance().getKeys();
        buffer.writeInt(keys.size());
        keys.forEach(key -> {
            int id = SyncedEntityData.instance().getInternalId(key);
            buffer.writeIdentifier(key.classKey().id());
            buffer.writeIdentifier(key.id());
            buffer.writeVarInt(id);
        });
    }

    public static SyncedEntityDataMappingS2CPacket decode(FriendlyByteBuf buffer) {
        int size = buffer.readInt();
        Map<Identifier, List<Pair<Identifier, Integer>>> keyMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Identifier classId = buffer.readIdentifier();
            Identifier keyId = buffer.readIdentifier();
            int id = buffer.readVarInt();
            keyMap.computeIfAbsent(classId, c -> new ArrayList<>()).add(Pair.of(keyId, id));
        }
        return new SyncedEntityDataMappingS2CPacket(keyMap);
    }

    @Override
    public void handle(ClientConfigurationNetworking.Context context) {
        GunMod.LOGGER.debug(HANDSHAKE, "Received synced key mappings from server");
        CountDownLatch block = new CountDownLatch(1);
        context.client().execute(() -> {
            if (!SyncedEntityData.instance().updateMappings(this.keyMap)) {
                context.responseSender().disconnect(Component.literal("Connection closed - [TacZ] Received unknown synced data keys."));
            }
            block.countDown();
        });
        try {
            block.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.responseSender().sendPacket(AcknowledgeC2SPacket.INSTANCE);
    }
}
