package com.tacz.guns.compat.jrei.rei;

import com.tacz.guns.api.item.*;
import me.shedaniel.rei.api.common.entry.comparison.EntryComparator;
import net.minecraft.world.item.ItemStack;

public class GunModREISubtype {
    public static EntryComparator<ItemStack> getAmmoSubtype() {
        return (context, stack) -> {
            if (stack.getItem() instanceof IAmmo iAmmo) {
                return iAmmo.getAmmoId(stack).hashCode();
            }
            return 0;
        };
    }

    public static EntryComparator<ItemStack> getGunSubtype() {
        return (context, stack) -> {
            if (stack.getItem() instanceof IGun iGun) {
                return iGun.getGunId(stack).hashCode();
            }
            return 0;
        };
    }

    public static EntryComparator<ItemStack> getAttachmentSubtype() {
        return (context, stack) -> {
            if (stack.getItem() instanceof IAttachment iAttachment) {
                return iAttachment.getAttachmentId(stack).hashCode();
            }
            return 0;
        };
    }

    public static EntryComparator<ItemStack> getTableSubType() {
        return (context, stack) -> {
            if (stack.getItem() instanceof IBlock iBlock) {
                return iBlock.getBlockId(stack).hashCode();
            }
            return 0;
        };
    }


    public static EntryComparator<ItemStack> getAmmoBoxSubtype() {
        return (context, stack) -> {
            if (stack.getItem() instanceof IAmmoBox iAmmoBox) {
                if (iAmmoBox.isAllTypeCreative(stack)) {
                    return "all_type_creative".hashCode();
                }
                if (iAmmoBox.isCreative(stack)) {
                    return "creative".hashCode();
                }
                return String.format("level_%d", iAmmoBox.getAmmoLevel(stack)).hashCode();
            }
            return 0;
        };
    }
}
