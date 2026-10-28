package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ClientMessagePlayerCrawl implements FabricPacket {
    public static final PacketType<ClientMessagePlayerCrawl> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_crawl"), ClientMessagePlayerCrawl::new);

    private final boolean isCrawl;

    public ClientMessagePlayerCrawl(boolean isCrawl) {
        this.isCrawl = isCrawl;
    }

    public ClientMessagePlayerCrawl(FriendlyByteBuf buf) {
        this.isCrawl = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isCrawl);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer entity, PacketSender responseSender) {
        if (entity == null) {
            return;
        }
        // 进入条件由服务端趴下逻辑统一验证；禁用功能后仍允许退出。
        IGunOperator.fromLivingEntity(entity).crawl(isCrawl);
    }
}
