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

public record ClientMessagePlayerAim(boolean isAim) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerAim> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_aim")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerAim> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, message -> message.isAim,
            ClientMessagePlayerAim::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessagePlayerAim message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            IGunOperator.fromLivingEntity(entity).aim(message.isAim);
        });
    }
}