package com.tacz.guns.network.message.event;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.EntityKillByGunEvent;
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

public class ServerMessageGunKill implements FabricPacket {
    public static final PacketType<ServerMessageGunKill> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_gunkill"), ServerMessageGunKill::new);

    private final int bulletId;
    private final int killEntityId;
    private final int attackerId;
    private final ResourceLocation gunId;
    private final ResourceLocation gunDisplayId;
    private final boolean isHeadShot;
    private final float baseDamage;
    private final float headshotMultiplier;

    public ServerMessageGunKill(FriendlyByteBuf buf) {
        this(
                buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readResourceLocation(), buf.readResourceLocation(),
                buf.readFloat(), buf.readBoolean(), buf.readFloat()
        );
    }

    public ServerMessageGunKill(int bulletId, int killEntityId, int attackerId, ResourceLocation gunId, ResourceLocation gunDisplayId, float baseDamage, boolean isHeadShot, float headshotMultiplier) {
        this.bulletId = bulletId;
        this.killEntityId = killEntityId;
        this.attackerId = attackerId;
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
        this.baseDamage = baseDamage;
        this.isHeadShot = isHeadShot;
        this.headshotMultiplier = headshotMultiplier;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(bulletId);
        buf.writeInt(killEntityId);
        buf.writeInt(attackerId);
        buf.writeResourceLocation(gunId);
        buf.writeResourceLocation(gunDisplayId);
        buf.writeFloat(baseDamage);
        buf.writeBoolean(isHeadShot);
        buf.writeFloat(headshotMultiplier);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        onKill(this);
    }

    @Environment(EnvType.CLIENT)
    private static void onKill(ServerMessageGunKill message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        @Nullable Entity bullet = level.getEntity(message.bulletId);
        @Nullable LivingEntity killedEntity = level.getEntity(message.killEntityId) instanceof LivingEntity livingEntity ? livingEntity : null;
        @Nullable LivingEntity attacker = level.getEntity(message.attackerId) instanceof LivingEntity livingEntity ? livingEntity : null;
        EntityKillByGunEvent event = new EntityKillByGunEvent(bullet, killedEntity, attacker, message.gunId, message.gunDisplayId, message.baseDamage, null, message.isHeadShot, message.headshotMultiplier, LogicalSide.CLIENT);
        EntityKillByGunEvent.CALLBACK.invoker().post(event);
    }
}
