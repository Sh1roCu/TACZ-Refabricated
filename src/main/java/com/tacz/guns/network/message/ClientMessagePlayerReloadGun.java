package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ClientMessagePlayerReloadGun implements CustomPacketPayload {
    public static final ClientMessagePlayerReloadGun INSTANCE = new ClientMessagePlayerReloadGun();
    public static final CustomPacketPayload.Type<ClientMessagePlayerReloadGun> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_reload")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerReloadGun> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessagePlayerReloadGun message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            IGunOperator.fromLivingEntity(entity).reload();
        });
    }
}