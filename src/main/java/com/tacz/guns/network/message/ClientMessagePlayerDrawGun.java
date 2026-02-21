package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class ClientMessagePlayerDrawGun implements CustomPacketPayload {
    public static final ClientMessagePlayerDrawGun INSTANCE = new ClientMessagePlayerDrawGun();
    public static final CustomPacketPayload.Type<ClientMessagePlayerDrawGun> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_draw_gun")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerDrawGun> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessagePlayerDrawGun message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            Inventory inventory = entity.getInventory();
            int selected = inventory.getSelectedSlot();
            IGunOperator.fromLivingEntity(entity).draw(() -> inventory.getItem(selected));
        });
    }
}