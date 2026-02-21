package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record ClientMessageCraft(Identifier recipeId, int menuId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageCraft> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "client_craft")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessageCraft> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, message -> message.recipeId,
            ByteBufCodecs.INT, message -> message.menuId,
            ClientMessageCraft::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessageCraft message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            if (entity.containerMenu.containerId == message.menuId && entity.containerMenu instanceof GunSmithTableMenu menu) {
                menu.doCraft(message.recipeId, entity);
            }
        });
    }
}