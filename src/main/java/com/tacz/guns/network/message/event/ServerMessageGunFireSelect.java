package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunFireSelectEvent;
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

public class ServerMessageGunFireSelect implements FabricPacket {
    public static final PacketType<ServerMessageGunFireSelect> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_gunfire_select"), ServerMessageGunFireSelect::new);

    private final int shooterId;
    private final ItemStack gunItemStack;

    public ServerMessageGunFireSelect(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readItem());
    }

    public ServerMessageGunFireSelect(int shooterId, ItemStack gunItemStack) {
        this.shooterId = shooterId;
        this.gunItemStack = gunItemStack;
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
        doClientEvent(this);
    }

    @Environment(EnvType.CLIENT)
    private static void doClientEvent(ServerMessageGunFireSelect message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        if (Minecraft.getInstance().player != null && message.shooterId == Minecraft.getInstance().player.getId()) {
            return;
        }
        if (level.getEntity(message.shooterId) instanceof LivingEntity shooter) {
            GunFireSelectEvent gunFireSelectEvent = new GunFireSelectEvent(shooter, message.gunItemStack, LogicalSide.CLIENT);
            GunFireSelectEvent.CALLBACK.invoker().post(gunFireSelectEvent);
        }
    }
}
