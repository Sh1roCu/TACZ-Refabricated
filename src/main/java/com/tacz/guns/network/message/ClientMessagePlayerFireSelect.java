package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ClientMessagePlayerFireSelect implements FabricPacket {
    public static final PacketType<ClientMessagePlayerFireSelect> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_fire_select"), ClientMessagePlayerFireSelect::new);

    public ClientMessagePlayerFireSelect() {

    }

    public ClientMessagePlayerFireSelect(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        IGunOperator.fromLivingEntity(player).fireSelect();
    }
}
