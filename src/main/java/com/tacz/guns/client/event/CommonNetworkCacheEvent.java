package com.tacz.guns.client.event;

import com.tacz.guns.client.resource.ClientIndexManager;
import com.tacz.guns.network.message.ServerMessageSyncGunPack;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.network.CommonNetworkCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

@Environment(EnvType.CLIENT)
public class CommonNetworkCacheEvent {
    public static void onClientPlayerLoggingIn(ClientPacketListener handler, PacketSender sender, Minecraft client) {
        ServerMessageSyncGunPack.clearPendingSync();
        if (handler.getConnection() == null || handler.getConnection().isMemoryConnection()) {
            return;
        }
        CommonAssetsManager.clearInstance();
        CommonNetworkCache.INSTANCE.clear();
        ClientIndexManager.clear();
    }

    public static void onClientPlayerLoggingOut(ClientPacketListener handler, Minecraft client) {
        ServerMessageSyncGunPack.clearPendingSync();
    }
}
