package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.ClientIndexManager;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.network.CommonNetworkCache;
import com.tacz.guns.resource.network.DataType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class ServerMessageSyncGunPack implements FabricPacket {
    public static final PacketType<ServerMessageSyncGunPack> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_sync_gunpack"), ServerMessageSyncGunPack::new);

    private final Map<DataType, Map<ResourceLocation, String>> cache;

    public ServerMessageSyncGunPack(FriendlyByteBuf buf) {
        this(buf.readMap(buf1 -> buf1.readEnum(DataType.class),
                buf2 -> buf2.readMap(FriendlyByteBuf::readResourceLocation, FriendlyByteBuf::readUtf)));
    }

    public ServerMessageSyncGunPack(Map<DataType, Map<ResourceLocation, String>> cache) {
        this.cache = cache;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(getCache(), FriendlyByteBuf::writeEnum, (buf1, map) ->
                buf1.writeMap(map, FriendlyByteBuf::writeResourceLocation, FriendlyByteBuf::writeUtf));
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        boolean remoteConnection = player.connection.getConnection() != null && !player.connection.getConnection().isMemoryConnection();
        doSync(this, remoteConnection);
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
