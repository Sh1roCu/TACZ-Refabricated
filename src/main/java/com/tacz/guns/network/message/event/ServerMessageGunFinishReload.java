package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.api.event.common.GunFinishReloadEvent;
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

public class ServerMessageGunFinishReload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunFinishReload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "server_message_gun_finish_reload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunFinishReload> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ServerMessageGunFinishReload::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int shooterId;
    private final ItemStack gunItemStack;

    public ServerMessageGunFinishReload(int shooterId, ItemStack gunItemStack) {
        this.shooterId = shooterId;
        this.gunItemStack = gunItemStack;
    }

    public static void encode(ServerMessageGunFinishReload message, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(message.shooterId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, message.gunItemStack);
    }

    public static ServerMessageGunFinishReload decode(RegistryFriendlyByteBuf buf) {
        int shooterId = buf.readVarInt();
        ItemStack gunItemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
        return new ServerMessageGunFinishReload(shooterId, gunItemStack);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageGunFinishReload message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> doClientEvent(message));
    }

    @Environment(EnvType.CLIENT)
    private static void doClientEvent(ServerMessageGunFinishReload message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.shooterId) instanceof LivingEntity shooter) {
            GunFinishReloadEvent.EVENT.invoker().post(new GunFinishReloadEvent(shooter, message.gunItemStack, LogicalSide.CLIENT));
        }
    }
}