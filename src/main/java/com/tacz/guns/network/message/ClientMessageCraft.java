package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ClientMessageCraft implements FabricPacket {
    public static final PacketType<ClientMessageCraft> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_craft"), ClientMessageCraft::new);

    private final ResourceLocation recipeId;
    private final int menuId;

    public ClientMessageCraft(FriendlyByteBuf buf) {
        this(buf.readResourceLocation(), buf.readVarInt());
    }

    public ClientMessageCraft(ResourceLocation recipeId, int menuId) {
        this.recipeId = recipeId;
        this.menuId = menuId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(recipeId);
        buf.writeVarInt(menuId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        if (player.containerMenu.containerId == menuId && player.containerMenu instanceof GunSmithTableMenu menu) {
            menu.doCraft(recipeId, player);
        }
    }
}
