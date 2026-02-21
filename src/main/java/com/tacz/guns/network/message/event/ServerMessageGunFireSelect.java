package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunFireSelectEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record ServerMessageGunFireSelect(int shooterId, ItemStack gunItemStack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageGunFireSelect> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "server_gun_fire_select")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageGunFireSelect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, message -> message.shooterId,
            ItemStack.STREAM_CODEC, message -> message.gunItemStack,
            ServerMessageGunFireSelect::new
    );

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerMessageGunFireSelect message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> doClientEvent(message));
    }

    @Environment(EnvType.CLIENT)
    private static void doClientEvent(ServerMessageGunFireSelect message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(message.shooterId) instanceof LivingEntity shooter) {
            GunFireSelectEvent gunFireSelectEvent = new GunFireSelectEvent(shooter, message.gunItemStack, LogicalSide.CLIENT);
            GunFireSelectEvent.CALLBACK.invoker().post(gunFireSelectEvent);
        }
    }
}