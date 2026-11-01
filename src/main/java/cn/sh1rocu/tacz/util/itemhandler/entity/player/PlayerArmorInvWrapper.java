package cn.sh1rocu.tacz.util.itemhandler.entity.player;

import cn.sh1rocu.tacz.util.itemhandler.InvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.RangedWrapper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerArmorInvWrapper extends RangedWrapper {
    private final Inventory inventoryPlayer;

    public PlayerArmorInvWrapper(Inventory inv) {
        super(new InvWrapper(inv), inv.items.size(), inv.items.size() + inv.armor.size());
        this.inventoryPlayer = inv;
    }

    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        EquipmentSlot equ = null;

        for (EquipmentSlot s : EquipmentSlot.values()) {
            if (s.getType() == EquipmentSlot.Type.ARMOR && s.getIndex() == slot) {
                equ = s;
                break;
            }
        }

        return equ != null && slot < 4 && !stack.isEmpty() && canEquip(stack, equ) ? super.insertItem(slot, stack, simulate) : stack;
    }


    private boolean canEquip(ItemStack stack, EquipmentSlot armorType) {
        return LivingEntity.getEquipmentSlotForItem(stack) == armorType;
    }

    public Inventory getInventoryPlayer() {
        return this.inventoryPlayer;
    }
}
