package com.tacz.guns.network.message;

import com.tacz.guns.inventory.AmmoStorageMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * A virtual entry interaction, not a vanilla inventory-slot click.
 */
public record ClientMessageAmmoStorageClick(int containerId, int entryId,
                                            AmmoStorageMenu.Action action) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageAmmoStorageClick> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "client_message_ammo_storage_click"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessageAmmoStorageClick> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ClientMessageAmmoStorageClick::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(ClientMessageAmmoStorageClick message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.containerId);
        buf.writeVarInt(message.entryId);
        buf.writeEnum(message.action);
    }

    public static ClientMessageAmmoStorageClick decode(RegistryFriendlyByteBuf buf) {
        return new ClientMessageAmmoStorageClick(buf.readVarInt(), buf.readVarInt(), buf.readEnum(AmmoStorageMenu.Action.class));
    }

    public static void handle(ClientMessageAmmoStorageClick message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer player = context.player();
            if (player != null && player.containerMenu instanceof AmmoStorageMenu menu
                    && menu.containerId == message.containerId) {
                menu.handleInteraction(player, message.entryId, message.action);
            }
        });
    }
}