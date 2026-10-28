package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunFirePostEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ServerMessageGunFirePost implements FabricPacket {
    public static final PacketType<ServerMessageGunFirePost> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_gunfire_post"), ServerMessageGunFirePost::decode);

    private final int shooterId;
    private final ItemStack gunItemStack;

    public ServerMessageGunFirePost(int shooterId, ItemStack gunItemStack) {
        this.shooterId = shooterId;
        this.gunItemStack = gunItemStack;
    }

    public static void encode(ServerMessageGunFirePost message, FriendlyByteBuf buf) {
    }

    public static ServerMessageGunFirePost decode(FriendlyByteBuf buf) {
        int shooterId = buf.readVarInt();
        ItemStack gunItemStack = buf.readItem();
        return new ServerMessageGunFirePost(shooterId, gunItemStack);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(shooterId);
        buf.writeItem(gunItemStack);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        doClientEvent();
    }

    @Environment(EnvType.CLIENT)
    private void doClientEvent() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (level.getEntity(shooterId) instanceof LivingEntity shooter) {
            GunFirePostEvent.EVENT.invoker().post(new GunFirePostEvent(shooter, gunItemStack, LogicalSide.CLIENT));
        }
    }
}
