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

public class ClientMessagePlayerBoltGun implements CustomPacketPayload {
    public static final ClientMessagePlayerBoltGun INSTANCE = new ClientMessagePlayerBoltGun();
    public static final CustomPacketPayload.Type<ClientMessagePlayerBoltGun> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_bolt_gun")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerBoltGun> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessagePlayerBoltGun message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            IGunOperator.fromLivingEntity(entity).bolt();
        });
    }
}