/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package cn.sh1rocu.tacz.util.itemhandler.entity.player;

import cn.sh1rocu.tacz.util.itemhandler.InvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.RangedWrapper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class PlayerArmorInvWrapper extends RangedWrapper {
    private final Inventory inventoryPlayer;

    public PlayerArmorInvWrapper(Inventory inv) {
        super(new InvWrapper(inv), inv.getNonEquipmentItems().size(), inv.getNonEquipmentItems().size() + 4);
        inventoryPlayer = inv;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        EquipmentSlot equ = null;
        for (EquipmentSlot s : EquipmentSlot.values()) {
            if (s.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && s.getIndex() == slot) {
                equ = s;
                break;
            }
        }
        // check if it's valid for the armor slot
        if (equ != null && slot < 4 && !stack.isEmpty() && getInventoryPlayer().player.getEquipmentSlotForItem(stack) == equ) {
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public Inventory getInventoryPlayer() {
        return inventoryPlayer;
    }
}
