package cn.sh1rocu.tacz.api.extension;

import com.tacz.guns.GunMod;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.List;

// Porting_Lib
public interface IEntityAdditionalSpawnData {
    ResourceLocation EXTRA_DATA_PACKET = new ResourceLocation(GunMod.MOD_ID, "extra_entity_spawn_data");

    void readSpawnData(FriendlyByteBuf buf);

    void writeSpawnData(FriendlyByteBuf buf);

    static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
        return getEntitySpawningPacket(entity, new ClientboundAddEntityPacket(entity));
    }

    static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity, Packet<ClientGamePacketListener> base) {
        if (entity instanceof IEntityAdditionalSpawnData extra) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeVarInt(entity.getId());
            extra.writeSpawnData(buf);
            Packet<ClientGamePacketListener> extraPacket = ServerPlayNetworking.createS2CPacket(IEntityAdditionalSpawnData.EXTRA_DATA_PACKET, buf);
            return new ClientboundBundlePacket(List.of(base, extraPacket));
        }
        return base;
    }
}