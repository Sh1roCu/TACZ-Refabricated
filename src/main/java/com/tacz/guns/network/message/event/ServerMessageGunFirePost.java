package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.api.event.common.GunFirePostEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ServerMessageGunFirePost implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunFirePost> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "server_message_gun_fire_post"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunFirePost> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ServerMessageGunFirePost::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int shooterId;
    private final ItemStack gunItemStack;

    public ServerMessageGunFirePost(int shooterId, ItemStack gunItemStack) {
        this.shooterId = shooterId;
        this.gunItemStack = gunItemStack;
    }

    public static void encode(ServerMessageGunFirePost message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.shooterId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.gunItemStack);
    }

    public static ServerMessageGunFirePost decode(RegistryFriendlyByteBuf buf) {
        int shooterId = buf.readVarInt();
        ItemStack gunItemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        return new ServerMessageGunFirePost(shooterId, gunItemStack);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageGunFirePost message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> doClientEvent(message));
    }

    @Environment(EnvType.CLIENT)
    private static void doClientEvent(ServerMessageGunFirePost message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.shooterId) instanceof LivingEntity shooter) {
            GunFirePostEvent.EVENT.invoker().post(new GunFirePostEvent(shooter, message.gunItemStack, LogicalSide.CLIENT));
        }
    }
}