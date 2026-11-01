package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ServerMessageCraft implements FabricPacket {
    public static final PacketType<ServerMessageCraft> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_craft"), ServerMessageCraft::new);

    private final int menuId;

    public ServerMessageCraft(int menuId) {
        this.menuId = menuId;
    }


    public ServerMessageCraft(FriendlyByteBuf buf) {
        this(buf.readVarInt());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(menuId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        updateScreen(menuId);
    }

    @Environment(EnvType.CLIENT)
    private static void updateScreen(int containerId) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.containerMenu.containerId == containerId && Minecraft.getInstance().screen instanceof GunSmithTableScreen screen) {
            screen.updateIngredientCount();
        }
    }
}
