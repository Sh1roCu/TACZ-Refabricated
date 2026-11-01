package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;

public class ClientMessagePlayerDrawGun implements FabricPacket {
    public static final PacketType<ClientMessagePlayerDrawGun> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_draw_gun"), ClientMessagePlayerDrawGun::new);

    public ClientMessagePlayerDrawGun() {

    }

    public ClientMessagePlayerDrawGun(FriendlyByteBuf buf) {
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
        Inventory inventory = player.getInventory();
        int selected = inventory.selected;
        IGunOperator.fromLivingEntity(player).draw(() -> inventory.getItem(selected));
    }
}
