package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public class ServerMessageGunHurt implements FabricPacket {
    public static final PacketType<ServerMessageGunHurt> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_gunhurt"), ServerMessageGunHurt::new);

    private final int bulletId;
    private final int hurtEntityId;
    private final int attackerId;
    private final ResourceLocation gunId;
    private final ResourceLocation gunDisplayId;
    private final float amount;
    private final boolean isHeadShot;
    private final float headshotMultiplier;

    public ServerMessageGunHurt(FriendlyByteBuf buf) {
        this(
                buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readResourceLocation(), buf.readResourceLocation(),
                buf.readFloat(), buf.readBoolean(), buf.readFloat()
        );
    }

    public ServerMessageGunHurt(int bulletId, int hurtEntityId, int attackerId, ResourceLocation gunId, ResourceLocation gunDisplayId,
                                float amount, boolean isHeadShot, float headshotMultiplier) {
        this.bulletId = bulletId;
        this.hurtEntityId = hurtEntityId;
        this.attackerId = attackerId;
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
        this.amount = amount;
        this.isHeadShot = isHeadShot;
        this.headshotMultiplier = headshotMultiplier;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(bulletId);
        buf.writeInt(hurtEntityId);
        buf.writeInt(attackerId);
        buf.writeResourceLocation(gunId);
        buf.writeResourceLocation(gunDisplayId);
        buf.writeFloat(amount);
        buf.writeBoolean(isHeadShot);
        buf.writeFloat(headshotMultiplier);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        onHurt(this);
    }

    @Environment(EnvType.CLIENT)
    private static void onHurt(ServerMessageGunHurt message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        @Nullable Entity bullet = level.getEntity(message.bulletId);
        @Nullable Entity hurtEntity = level.getEntity(message.hurtEntityId);
        @Nullable LivingEntity attacker = level.getEntity(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        EntityHurtByGunEvent.Post event = new EntityHurtByGunEvent.Post(bullet, hurtEntity, attacker, message.gunId, message.gunDisplayId, message.amount, null, message.isHeadShot, message.headshotMultiplier, LogicalSide.CLIENT);
        EntityHurtByGunEvent.POST.invoker().post(event);
    }
}
