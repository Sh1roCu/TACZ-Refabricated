package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.client.event.SwapItemWithOffHand;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ServerMessageSwapItem implements FabricPacket {
    public static final PacketType<ServerMessageSwapItem> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_swap_item"), ServerMessageSwapItem::new);

    public ServerMessageSwapItem() {

    }

    public ServerMessageSwapItem(FriendlyByteBuf buf) {
        this();
    }

    @Override
    public void write(FriendlyByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(LocalPlayer player, PacketSender responseSender) {
        SwapItemWithOffHand.CALLBACK.invoker().post(new SwapItemWithOffHand());
    }
}
