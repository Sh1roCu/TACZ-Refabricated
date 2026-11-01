package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ServerMessageLevelUp implements FabricPacket {
    public static final PacketType<ServerMessageLevelUp> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "s2c_levelup"), ServerMessageLevelUp::new);

    private final ItemStack gun;
    private final int level;

    public ServerMessageLevelUp(FriendlyByteBuf buf) {
        this(buf.readItem(), buf.readInt());
    }

    public ServerMessageLevelUp(ItemStack gun, int level) {
        this.gun = gun;
        this.level = level;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeItem(gun);
        buf.writeInt(level);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public void handle(LocalPlayer player, PacketSender responseSender) {
        onLevelUp(this);
    }

    @Environment(EnvType.CLIENT)
    private static void onLevelUp(ServerMessageLevelUp message) {
        int level = message.getLevel();
        ItemStack gun = message.getGun();
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        // TODO 在完成了枪械升级逻辑后，解封下面的代码
                /*
                if (GunLevelManager.DAMAGE_UP_LEVELS.contains(level)) {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz.level_up"),
                            Component.translatable("toast.tacz.sub.damage_up")));
                } else if (level >= GunLevelManager.MAX_LEVEL) {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz.level_up"),
                            Component.translatable("toast.tacz.sub.final_level")));
                } else {
                    Minecraft.getInstance().getToasts().addToast(new GunLevelUpToast(gun,
                            Component.translatable("toast.tacz.level_up"),
                            Component.translatable("toast.tacz.sub.level_up")));
                }*/
    }

    public ItemStack getGun() {
        return this.gun;
    }

    public int getLevel() {
        return this.level;
    }
}
