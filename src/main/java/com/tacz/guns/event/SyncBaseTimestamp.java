package com.tacz.guns.event;

import cn.sh1rocu.tacz.api.event.EntityJoinLevelEvent;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.ServerMessageSyncBaseTimestamp;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SyncBaseTimestamp {
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player && !event.getLevel().isClientSide()) {
            NetworkHandler.sendToClientPlayer(new ServerMessageSyncBaseTimestamp(), (ServerPlayer) player);
        }
    }
}
