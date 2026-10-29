package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientIndexManager;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.network.CommonNetworkCache;
import com.tacz.guns.resource.network.DataType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ServerMessageSyncGunPack implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSyncGunPack> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "server_sync_gun_pack")
    );
    public static final StreamCodec<FriendlyByteBuf, ServerMessageSyncGunPack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.fromCodec(DataType.CODEC),
                    ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.STRING_UTF8)),
            ServerMessageSyncGunPack::getCache,
            ServerMessageSyncGunPack::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final Map<DataType, Map<ResourceLocation, String>> cache;

    public ServerMessageSyncGunPack(Map<DataType, Map<ResourceLocation, String>> cache) {
        this.cache = cache;
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageSyncGunPack message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
                    var connection = context.client().getConnection();
                    boolean remoteConnection = connection != null && !connection.getConnection().isMemoryConnection();
                    doSync(message, remoteConnection);
                }
        );
    }


    public Map<DataType, Map<ResourceLocation, String>> getCache() {
        return cache;
    }

    @Environment(EnvType.CLIENT)
    private static void doSync(ServerMessageSyncGunPack message, boolean remoteConnection) {
        if (remoteConnection) {
            CommonAssetsManager.clearInstance();
        }
        CommonNetworkCache.INSTANCE.fromNetwork(message.cache);
        // 通知客户端重新构建ClientIndex
        ClientIndexManager.reload();
    }
}