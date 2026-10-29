package com.tacz.guns.network.message;

import com.tacz.guns.client.sound.SoundPlayManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * 仅在服务端确认本次主动卸弹实际减少了枪内弹药后发送给操作者。
 */
public class ServerMessageGunUnload implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunUnload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "server_message_gun_unload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunUnload> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ServerMessageGunUnload::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final ResourceLocation gunId;
    private final ResourceLocation gunDisplayId;

    public ServerMessageGunUnload(ResourceLocation gunId, ResourceLocation gunDisplayId) {
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
    }

    public static void encode(ServerMessageGunUnload message, RegistryFriendlyByteBuf buf) {
        buf.writeResourceLocation(message.gunId);
        buf.writeResourceLocation(message.gunDisplayId);
    }

    public static ServerMessageGunUnload decode(RegistryFriendlyByteBuf buf) {
        return new ServerMessageGunUnload(buf.readResourceLocation(), buf.readResourceLocation());
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageGunUnload message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> playSound(message));
    }

    @Environment(EnvType.CLIENT)
    private static void playSound(ServerMessageGunUnload message) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            SoundPlayManager.playUnloadSound(player, message.gunId, message.gunDisplayId);
        }
    }
}