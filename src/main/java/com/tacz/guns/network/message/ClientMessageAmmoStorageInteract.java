package com.tacz.guns.network.message;

import cn.sh1rocu.tacz.api.extension.IEntityPersistentData;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.block.entity.AmmoStorageBlockEntity;
import com.tacz.guns.util.AmmoStorageHelper;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientMessageAmmoStorageInteract implements FabricPacket {
    public static final PacketType<ClientMessageAmmoStorageInteract> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_ammo_storage_interact"), ClientMessageAmmoStorageInteract::new);

    private static final String LAST_PICKUP_TAG = "TacAmmoStorageLastPickup";
    private static final String LAST_ACCUMULATE_TAG = "TacAmmoStorageLastAccumulate";
    private static final String ACCUMULATE_AMMO_TAG = "TacAmmoStorageAccumulateAmmo";
    private static final String ACCUMULATE_COUNT_TAG = "TacAmmoStorageAccumulateCount";
    private static final long PICKUP_COOLDOWN_TICKS = 5L;
    private static final long ACCUMULATE_WINDOW_TICKS = 20L;

    private final BlockPos pos;

    public ClientMessageAmmoStorageInteract(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public ClientMessageAmmoStorageInteract(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        if (player == null || player.isSpectator()) {
            return;
        }
        long gameTime = player.level().getGameTime();
        long lastPickup = ((IEntityPersistentData) player).tacz$getPersistentData().getLong(LAST_PICKUP_TAG);
        if (lastPickup > 0L && gameTime - lastPickup < PICKUP_COOLDOWN_TICKS) {
            return;
        }
        if (player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > 36.0D) {
            return;
        }
        ItemStack gunStack = player.getMainHandItem();
        if (!(gunStack.getItem() instanceof IGun gun)) {
            return;
        }
        ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(gun.getGunId(gunStack))
                .map(index -> index.getGunData().getAmmoId())
                .orElse(null);
        if (ammoId == null) {
            return;
        }
        BlockEntity blockEntity = player.level().getBlockEntity(pos);
        if (!(blockEntity instanceof AmmoStorageBlockEntity storage)) {
            return;
        }
        int amount = AmmoStorageHelper.getStackSize(AmmoStorageHelper.createAmmoStack(ammoId, 1));
        ItemStack extracted = storage.extractAmmo(ammoId, amount, false);
        if (extracted.isEmpty()) {
            ((IEntityPersistentData) player).tacz$getPersistentData().putLong(LAST_PICKUP_TAG, gameTime);
            player.displayClientMessage(Component.translatable("message.tacz.ammo_storage.no_gun_stock"), true);
            return;
        }
        ((IEntityPersistentData) player).tacz$getPersistentData().putLong(LAST_PICKUP_TAG, gameTime);
        int count = extracted.getCount();
        if (!player.getInventory().add(extracted)) {
            player.drop(extracted, false);
        }
        int accumulatedCount = getAccumulatedCount(player, ammoId, count, gameTime);
        player.displayClientMessage(Component.translatable("message.tacz.ammo_storage.picked", AmmoStorageHelper.createAmmoStack(ammoId, 1).getHoverName(), accumulatedCount), true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.ARMOR_EQUIP_IRON, SoundSource.PLAYERS, 0.8F, 1.0F);
    }

    private static int getAccumulatedCount(ServerPlayer player, ResourceLocation ammoId, int count, long gameTime) {
        String ammoKey = ammoId.toString();
        long lastAccumulate = ((IEntityPersistentData) player).tacz$getPersistentData().getLong(LAST_ACCUMULATE_TAG);
        String lastAmmoKey = ((IEntityPersistentData) player).tacz$getPersistentData().getString(ACCUMULATE_AMMO_TAG);
        int accumulated = count;
        if (lastAccumulate > 0L && gameTime - lastAccumulate <= ACCUMULATE_WINDOW_TICKS && ammoKey.equals(lastAmmoKey)) {
            accumulated += ((IEntityPersistentData) player).tacz$getPersistentData().getInt(ACCUMULATE_COUNT_TAG);
        }
        ((IEntityPersistentData) player).tacz$getPersistentData().putLong(LAST_ACCUMULATE_TAG, gameTime);
        ((IEntityPersistentData) player).tacz$getPersistentData().putString(ACCUMULATE_AMMO_TAG, ammoKey);
        ((IEntityPersistentData) player).tacz$getPersistentData().putInt(ACCUMULATE_COUNT_TAG, accumulated);
        return accumulated;
    }
}
