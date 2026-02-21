package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record ClientMessagePlayerShoot(long timestamp) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerShoot> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_shoot")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerShoot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, message -> message.timestamp,
            ClientMessagePlayerShoot::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 这里的 timestamp 应该是基于 base timestamp 的相对值
     */

    public static void handle(ClientMessagePlayerShoot message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            IGunOperator.fromLivingEntity(entity).shoot(entity::getXRot, entity::getYRot, message.timestamp);
        });
    }
}