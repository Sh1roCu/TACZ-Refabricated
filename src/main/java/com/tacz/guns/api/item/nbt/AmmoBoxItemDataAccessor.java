package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.IGun;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public interface AmmoBoxItemDataAccessor extends IAmmoBox {
    String AMMO_ID_TAG = "AmmoId";
    String AMMO_COUNT_TAG = "AmmoCount";
    String CREATIVE_TAG = "Creative";
    String ALL_TYPE_CREATIVE_TAG = "AllTypeCreative";
    String LEVEL_TAG = "Level";

    @Override
    default Identifier getAmmoId(ItemStack ammoBox) {
        CompoundTag tag = ammoBox.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(AMMO_ID_TAG)) {
            return Identifier.parse(tag.getStringOr(AMMO_ID_TAG, ""));
        }
        return DefaultAssets.EMPTY_AMMO_ID;
    }

    @Override
    default void setAmmoId(ItemStack ammoBox, Identifier ammoId) {
        ammoBox.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            tag.putString(AMMO_ID_TAG, ammoId.toString());
        }));
    }

    @Override
    default int getAmmoCount(ItemStack ammoBox) {
        CompoundTag tag = ammoBox.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (isAllTypeCreative(ammoBox) || isCreative(ammoBox)) {
            return Integer.MAX_VALUE;
        }
        if (tag.contains(AMMO_COUNT_TAG)) {
            return tag.getIntOr(AMMO_COUNT_TAG, 0);
        }
        return 0;
    }

    @Override
    default void setAmmoCount(ItemStack ammoBox, int count) {
        ammoBox.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            if (isCreative(ammoBox)) {
                tag.putInt(AMMO_COUNT_TAG, Integer.MAX_VALUE);
                return;
            }
            tag.putInt(AMMO_COUNT_TAG, count);
        }));
    }

    @Override
    default boolean isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox) {
        if (gun.getItem() instanceof IGun iGun && ammoBox.getItem() instanceof IAmmoBox iAmmoBox) {
            if (isAllTypeCreative(ammoBox)) {
                return true;
            }
            Identifier ammoId = iAmmoBox.getAmmoId(ammoBox);
            if (ammoId.equals(DefaultAssets.EMPTY_AMMO_ID)) {
                return false;
            }
            Identifier gunId = iGun.getGunId(gun);
            return TimelessAPI.getCommonGunIndex(gunId).map(gunIndex -> gunIndex.getGunData().getAmmoId().equals(ammoId)).orElse(false);
        }
        return false;
    }

    @Override
    default ItemStack setAmmoLevel(ItemStack ammoBox, int level) {
        ammoBox.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            tag.putInt(LEVEL_TAG, Math.max(level, 0));
        }));
        return ammoBox;
    }

    @Override
    default int getAmmoLevel(ItemStack ammoBox) {
        CompoundTag tag = ammoBox.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(LEVEL_TAG)) {
            return tag.getIntOr(LEVEL_TAG, 0);
        }
        return 0;
    }

    @Override
    default boolean isCreative(ItemStack ammoBox) {
        CompoundTag tag = ammoBox.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(CREATIVE_TAG)) {
            return tag.getBooleanOr(CREATIVE_TAG, false);
        }
        return false;
    }

    @Override
    default boolean isAllTypeCreative(ItemStack ammoBox) {
        CompoundTag tag = ammoBox.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(ALL_TYPE_CREATIVE_TAG)) {
            return tag.getBooleanOr(ALL_TYPE_CREATIVE_TAG, false);
        }
        return false;
    }

    @Override
    default ItemStack setCreative(ItemStack ammoBox, boolean isAllType) {
        ammoBox.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            if (isAllType) {
                // 移除可能存在的创造模式标签
                if (tag.contains(CREATIVE_TAG)) {
                    tag.remove(CREATIVE_TAG);
                }
                tag.putBoolean(ALL_TYPE_CREATIVE_TAG, true);
                return;
            }
            // 移除可能存在的全类型标签
            if (tag.contains(ALL_TYPE_CREATIVE_TAG)) {
                tag.remove(ALL_TYPE_CREATIVE_TAG);
            }
            tag.putBoolean(CREATIVE_TAG, true);
        }));
        return ammoBox;
    }
}