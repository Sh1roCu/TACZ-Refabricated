package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ServerMessageSound(int entityId, Identifier gunId, Identifier gunDisplayId,
                                 MessageSound messageSound) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageSound> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "server_sound")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageSound> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ServerMessageSound::entityId,
            Identifier.STREAM_CODEC, ServerMessageSound::gunId,
            Identifier.STREAM_CODEC, ServerMessageSound::gunDisplayId,
            MessageSound.STREAM_CODEC, ServerMessageSound::messageSound,
            ServerMessageSound::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerMessageSound message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> SoundPlayManager.playMessageSound(message));
    }

    public record MessageSound(String soundName, float volume, float pitch, int distance) {
        public static final StreamCodec<RegistryFriendlyByteBuf, MessageSound> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, MessageSound::soundName,
                ByteBufCodecs.FLOAT, MessageSound::volume,
                ByteBufCodecs.FLOAT, MessageSound::pitch,
                ByteBufCodecs.INT, MessageSound::distance,
                MessageSound::new
        );
    }
}