package com.tacz.guns.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

public enum GunTooltipPart {
    DESCRIPTION,
    AMMO_INFO,
    BASE_INFO,
    EXTRA_DAMAGE_INFO,
    UPGRADES_TIP,
    PACK_INFO;

    private final int mask = 1 << this.ordinal();

    public int getMask() {
        return this.mask;
    }

    public static int getHideFlags(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains("HideFlags")) {
            return tag.getIntOr("HideFlags", 0);
        }
        return 0;
    }

    public static void setHideFlags(ItemStack stack, int mask) {
        // In 1.21.11, HIDE_TOOLTIP and HIDE_ADDITIONAL_TOOLTIP are replaced by TOOLTIP_DISPLAY
        if (mask != 0) {
            TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
            stack.set(DataComponents.TOOLTIP_DISPLAY, display.withHidden(DataComponents.CUSTOM_DATA, true));
        }
    }
}
