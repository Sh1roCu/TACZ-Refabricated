package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.client.sound.SoundPlayManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ServerMessageSound implements FabricPacket {
    public static final PacketType<ServerMessageSound> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_sound"), ServerMessageSound::new);

    private final int entityId;
    private final ResourceLocation gunId;
    private final ResourceLocation gunDisplayId;
    private final String soundName;
    private final float volume;
    private final float pitch;
    private final int distance;

    public ServerMessageSound(FriendlyByteBuf buf) {
        this(buf.readVarInt(), buf.readResourceLocation(), buf.readResourceLocation(), buf.readUtf(), buf.readFloat(), buf.readFloat(), buf.readInt());
    }

    public ServerMessageSound(int entityId, ResourceLocation gunId, ResourceLocation gunDisplayId, String soundName, float volume, float pitch, int distance) {
        this.entityId = entityId;
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
        this.distance = distance;
    }

    public ServerMessageSound(int entityId, ResourceLocation gunId, String soundName, float volume, float pitch, int distance) {
        this(entityId, gunId, DefaultAssets.DEFAULT_GUN_DISPLAY_ID, soundName, volume, pitch, distance);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(entityId);
        buf.writeResourceLocation(gunId);
        buf.writeResourceLocation(gunDisplayId);
        buf.writeUtf(soundName);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(distance);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        SoundPlayManager.playMessageSound(this);
    }

    public int getEntityId() {
        return entityId;
    }

    public ResourceLocation getGunId() {
        return gunId;
    }

    public ResourceLocation getGunDisplayId() {
        return gunDisplayId;
    }

    public String getSoundName() {
        return soundName;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public int getDistance() {
        return distance;
    }
}
