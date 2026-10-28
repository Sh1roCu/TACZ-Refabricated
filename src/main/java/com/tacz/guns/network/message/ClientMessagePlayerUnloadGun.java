package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClientMessagePlayerUnloadGun implements FabricPacket {
    public static final PacketType<ClientMessagePlayerUnloadGun> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_unload"), ClientMessagePlayerUnloadGun::decode);

    private final int slot;
    private final ResourceLocation gunId;

    public ClientMessagePlayerUnloadGun(int slot, ResourceLocation gunId) {
        this.slot = slot;
        this.gunId = gunId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(slot);
        buf.writeResourceLocation(gunId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public static ClientMessagePlayerUnloadGun decode(FriendlyByteBuf buf) {
        return new ClientMessagePlayerUnloadGun(buf.readVarInt(), buf.readResourceLocation());
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        if (player == null) {
            return;
        }
        if (!player.isAlive() || player.isSpectator()
                || slot < 0 || slot >= 9 || player.getInventory().selected != slot) {
            showCannotUnload(player);
            return;
        }
        var stack = player.getMainHandItem();
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null || !gunId.equals(gun.getGunId(stack))) {
            showCannotUnload(player);
            return;
        }
        var index = TimelessAPI.getCommonGunIndex(gunId).orElse(null);
        if (index == null) {
            showCannotUnload(player);
            return;
        }
        if (!index.canUnloadAmmo() || gun.useInventoryAmmo(stack)) {
            player.displayClientMessage(Component.translatable("message.tacz.unload.unsupported"), true);
            return;
        }
        Bolt bolt = index.getGunData().getBolt();
        long ammoBefore = getLoadedAmmoCount(gun, stack, bolt);
        if (ammoBefore == 0) {
            player.displayClientMessage(Component.translatable("message.tacz.unload.no_ammo"), true);
            return;
        }
        var gunDisplayId = gun.getGunDisplayId(stack);
        IGunOperator.fromLivingEntity(player).unloadAmmo();
        player.inventoryMenu.broadcastChanges();
        if (getLoadedAmmoCount(gun, stack, bolt) < ammoBefore) {
            NetworkHandler.sendToClientPlayer(new ServerMessageGunUnload(gunId, gunDisplayId), player);
        } else {
            showCannotUnload(player);
        }
    }

    private static long getLoadedAmmoCount(IGun gun, ItemStack stack, Bolt bolt) {
        return Math.max(0L, gun.getCurrentAmmoCount(stack))
                + (bolt != Bolt.OPEN_BOLT && gun.hasBulletInBarrel(stack) ? 1L : 0L);
    }

    private static void showCannotUnload(ServerPlayer player) {
        player.displayClientMessage(Component.translatable("message.tacz.unload.cannot"), true);
    }
}
