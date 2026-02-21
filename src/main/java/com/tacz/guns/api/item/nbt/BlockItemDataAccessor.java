package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface BlockItemDataAccessor extends IBlock {
    String BLOCK_ID = "BlockId";

    @Override
    @Nonnull
    default Identifier getBlockId(ItemStack block) {
        CompoundTag nbt = block.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (nbt.contains(BLOCK_ID)) {
            Identifier gunId = Identifier.tryParse(nbt.getStringOr(BLOCK_ID, ""));
            return Objects.requireNonNullElse(gunId, DefaultAssets.EMPTY_BLOCK_ID);
        }
        return DefaultAssets.EMPTY_BLOCK_ID;
    }

    @Override
    default void setBlockId(ItemStack block, @Nullable Identifier blockId) {
        block.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            if (blockId != null) {
                tag.putString(BLOCK_ID, blockId.toString());
                return;
            }
            tag.putString(BLOCK_ID, DefaultAssets.EMPTY_BLOCK_ID.toString());
        }));
    }

}