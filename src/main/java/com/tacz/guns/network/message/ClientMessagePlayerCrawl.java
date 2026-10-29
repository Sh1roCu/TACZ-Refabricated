package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record ClientMessagePlayerCrawl(boolean isCrawl) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessagePlayerCrawl> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "client_player_crawl")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessagePlayerCrawl> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, message -> message.isCrawl,
            ClientMessagePlayerCrawl::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientMessagePlayerCrawl message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            ServerPlayer entity = context.player();
            if (entity == null) {
                return;
            }
            // 进入条件由服务端趴下逻辑统一验证；禁用功能后仍允许退出。
            IGunOperator.fromLivingEntity(entity).crawl(message.isCrawl);
        });
    }
}