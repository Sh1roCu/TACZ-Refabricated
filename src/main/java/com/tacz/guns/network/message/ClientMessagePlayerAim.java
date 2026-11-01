package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ClientMessagePlayerAim implements FabricPacket {
    public static final PacketType<ClientMessagePlayerAim> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_aim"), ClientMessagePlayerAim::new);

    private final boolean isAim;

    public ClientMessagePlayerAim(boolean isAim) {
        this.isAim = isAim;
    }

    public ClientMessagePlayerAim(FriendlyByteBuf buf) {
        this.isAim = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(isAim);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        IGunOperator.fromLivingEntity(player).aim(isAim);
    }
}
