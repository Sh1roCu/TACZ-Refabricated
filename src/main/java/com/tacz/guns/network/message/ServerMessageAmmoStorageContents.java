package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.AmmoStorageEntry;
import com.tacz.guns.inventory.AmmoStorageMenu;
import io.netty.handler.codec.DecoderException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record ServerMessageAmmoStorageContents(int containerId, int capacityStacks, long usedStacks,
                                               List<AmmoStorageEntry> entries) implements FabricPacket {
    public static final PacketType<ServerMessageAmmoStorageContents> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_ammo_storage_contents"),
            ServerMessageAmmoStorageContents::decode);

    public ServerMessageAmmoStorageContents {
        entries = List.copyOf(entries);
    }

    public static ServerMessageAmmoStorageContents decode(FriendlyByteBuf buf) {
        int containerId = buf.readVarInt();
        int capacityStacks = buf.readVarInt();
        long usedStacks = buf.readVarLong();
        int size = buf.readVarInt();
        if (capacityStacks < 0 || usedStacks < 0 || size < 0 || size > AmmoStorageMenu.STORAGE_SLOT_COUNT) {
            throw new DecoderException("Invalid ammo storage entry count: " + size);
        }
        List<AmmoStorageEntry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int id = buf.readVarInt();
            ItemStack sample = buf.readItem();
            long count = buf.readVarLong();
            long remaining = buf.readVarLong();
            if (id < 0 || sample.isEmpty() || count <= 0 || remaining < 0) {
                throw new DecoderException("Invalid ammo storage entry");
            }
            entries.add(new AmmoStorageEntry(id, sample, count, remaining));
        }
        return new ServerMessageAmmoStorageContents(containerId, capacityStacks, usedStacks, entries);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(capacityStacks);
        buf.writeVarLong(usedStacks);
        buf.writeVarInt(entries.size());
        for (AmmoStorageEntry entry : entries) {
            buf.writeVarInt(entry.id());
            buf.writeItem(entry.sample());
            buf.writeVarLong(entry.count());
            buf.writeVarLong(entry.remainingCapacity());
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        updateContents();
    }

    @Environment(EnvType.CLIENT)
    private void updateContents() {
        var player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu instanceof AmmoStorageMenu menu
                && menu.containerId == containerId) {
            menu.setContents(capacityStacks, usedStacks, entries);
        }
    }
}
