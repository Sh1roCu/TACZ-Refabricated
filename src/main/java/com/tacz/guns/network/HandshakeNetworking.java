package com.tacz.guns.network;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.message.handshake.SyncedEntityDataMappingS2CPacket;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationNetworkHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class HandshakeNetworking {
    public record SyncedEntityDataTask(FabricServerConfigurationNetworkHandler handler) implements ConfigurationTask {
        public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(
                Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "synced_entity_data_mapping").toString()
        );

        @Override
        public void start(@NotNull Consumer<Packet<?>> task) {
            run(customPacketPayload -> task.accept(new ClientboundCustomPayloadPacket(customPacketPayload)));
        }

        @Override
        public @NotNull ConfigurationTask.Type type() {
            return TYPE;
        }

        public void run(@NotNull Consumer<CustomPacketPayload> consumer) {
            consumer.accept(new SyncedEntityDataMappingS2CPacket());
            handler.completeTask(type());
        }
    }
}
