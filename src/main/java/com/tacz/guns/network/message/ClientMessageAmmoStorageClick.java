package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.AmmoStorageMenu;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * A virtual entry interaction, not a vanilla inventory-slot click.
 */
public record ClientMessageAmmoStorageClick(int containerId, int entryId,
                                            AmmoStorageMenu.Action action) implements FabricPacket {
    public static final PacketType<ClientMessageAmmoStorageClick> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_ammo_storage_click"), ClientMessageAmmoStorageClick::decode);

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(containerId);
        buf.writeVarInt(entryId);
        buf.writeEnum(action);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static ClientMessageAmmoStorageClick decode(FriendlyByteBuf buf) {
        return new ClientMessageAmmoStorageClick(buf.readVarInt(), buf.readVarInt(), buf.readEnum(AmmoStorageMenu.Action.class));
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        if (player != null && player.containerMenu instanceof AmmoStorageMenu menu
                && menu.containerId == containerId) {
            menu.handleInteraction(player, entryId, action);
        }
    }
}
