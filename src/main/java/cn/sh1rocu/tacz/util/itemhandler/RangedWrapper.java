package cn.sh1rocu.tacz.util.itemhandler;

import com.google.common.base.Preconditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class RangedWrapper implements IItemHandlerModifiable {
    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public int getSlots() {
        return maxSlot - minSlot;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (checkSlot(slot)) {
            return compose.getStackInSlot(slot + minSlot);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (checkSlot(slot)) {
            return compose.insertItem(slot + minSlot, stack, simulate);
        }

        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (checkSlot(slot)) {
            return compose.extractItem(slot + minSlot, amount, simulate);
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (checkSlot(slot)) {
            compose.setStackInSlot(slot + minSlot, stack);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (checkSlot(slot)) {
            return compose.getSlotLimit(slot + minSlot);
        }

        return 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (checkSlot(slot)) {
            return compose.isItemValid(slot + minSlot, stack);
        }

        return false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + minSlot < maxSlot;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < getSlots(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                CompoundTag itemTag = (CompoundTag) ItemStack.OPTIONAL_CODEC.encodeStart(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), getStackInSlot(i)).getOrThrow();
                itemTag.putInt("Slot", i);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", getSlots());
        return nbt;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        int size = nbt.contains("Size") ? nbt.getIntOr("Size", getSlots()) : getSlots();
        ListTag tagList = nbt.getListOrEmpty("Items");
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompoundOrEmpty(i);
            int slot = itemTags.getIntOr("Slot", 0);

            if (slot >= 0 && slot < size) {
                ItemStack.OPTIONAL_CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), itemTags).result().ifPresent(stack -> setStackInSlot(slot, stack));
            }
        }
    }
}
