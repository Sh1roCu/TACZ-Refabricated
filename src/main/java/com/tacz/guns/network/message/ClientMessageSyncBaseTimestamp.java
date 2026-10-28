package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.util.time.GunTime;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ClientMessageSyncBaseTimestamp implements FabricPacket {
    public static final PacketType<ClientMessageSyncBaseTimestamp> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_sync_base_timestamp"), ClientMessageSyncBaseTimestamp::new);

    private static final Marker MARKER = MarkerFactory.getMarker("SYNC_BASE_TIMESTAMP");

    public ClientMessageSyncBaseTimestamp(FriendlyByteBuf buf) {
        this();
    }

    public ClientMessageSyncBaseTimestamp() {

    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer entity, PacketSender responseSender) {
        long timestamp = GunTime.nowMillis();
        if (entity == null) {
            return;
        }
        ShooterDataHolder dataHolder = IGunOperator.fromLivingEntity(entity).getDataHolder();
        dataHolder.baseTimestamp = timestamp;
        GunMod.LOGGER.debug(MARKER, "Update server base timestamp: {}", dataHolder.baseTimestamp);
    }
}
