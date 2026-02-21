package com.tacz.guns.api.item.nbt;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAttachment;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public interface AttachmentItemDataAccessor extends IAttachment {
    String ATTACHMENT_ID_TAG = "AttachmentId";
    String SKIN_ID_TAG = "Skin";
    String ZOOM_NUMBER_TAG = "ZoomNumber";
    String LASER_COLOR_TAG = "LaserColor";

    // 仅检查给定的 CompoundTag 是否具有配件 ID ，不校验其是否存在
    static boolean isAttachmentLike(CompoundTag tag) {
        return tag.contains(ATTACHMENT_ID_TAG);
    }

    @Nonnull
    static Identifier getAttachmentIdFromTag(@Nullable CompoundTag nbt) {
        if (nbt == null) {
            return DefaultAssets.EMPTY_ATTACHMENT_ID;
        }
        if (isAttachmentLike(nbt)) {
            Identifier attachmentId = Identifier.tryParse(nbt.getStringOr(ATTACHMENT_ID_TAG, ""));
            return Objects.requireNonNullElse(attachmentId, DefaultAssets.EMPTY_ATTACHMENT_ID);
        }
        return DefaultAssets.EMPTY_ATTACHMENT_ID;
    }

    static int getZoomNumberFromTag(@Nullable CompoundTag nbt) {
        if (nbt == null) {
            return 0;
        }
        if (nbt.contains(ZOOM_NUMBER_TAG)) {
            return nbt.getIntOr(ZOOM_NUMBER_TAG, 0);
        }
        return 0;
    }

    static void setZoomNumberToTag(CompoundTag nbt, int zoomNumber) {
        nbt.putInt(ZOOM_NUMBER_TAG, zoomNumber);
    }

    static void setLaserColorToTag(CompoundTag nbt, int color) {
        nbt.putInt(LASER_COLOR_TAG, color);
    }

    @Override
    @Nonnull
    default Identifier getAttachmentId(ItemStack attachmentStack) {
        CompoundTag nbt = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return getAttachmentIdFromTag(nbt);
    }

    @Override
    default void setAttachmentId(ItemStack attachmentStack, @Nullable Identifier attachmentId) {
        attachmentStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            if (attachmentId != null) {
                tag.putString(ATTACHMENT_ID_TAG, attachmentId.toString());
            }
        }));
    }

    @Override
    @Nullable
    default Identifier getSkinId(ItemStack attachmentStack) {
        CompoundTag nbt = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (nbt.contains(SKIN_ID_TAG)) {
            return Identifier.tryParse(nbt.getStringOr(SKIN_ID_TAG, ""));
        }
        return null;
    }

    @Override
    default void setSkinId(ItemStack attachmentStack, @Nullable Identifier skinId) {
        attachmentStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            if (skinId != null) {
                tag.putString(SKIN_ID_TAG, skinId.toString());
            } else {
                tag.remove(SKIN_ID_TAG);
            }
        }));
    }

    @Override
    default int getZoomNumber(ItemStack attachmentStack) {
        CompoundTag nbt = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return getZoomNumberFromTag(nbt);
    }

    @Override
    default void setZoomNumber(ItemStack attachmentStack, int zoomNumber) {
        attachmentStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            setZoomNumberToTag(tag, zoomNumber);
        }));
    }

    @Override
    default boolean hasCustomLaserColor(ItemStack attachmentStack) {
        CompoundTag nbt = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return nbt.contains(LASER_COLOR_TAG);
    }

    @Override
    default int getLaserColor(ItemStack attachmentStack) {
        CompoundTag nbt = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!hasCustomLaserColor(attachmentStack)) {
            return 0xFF0000;
        }
        return nbt.getIntOr(LASER_COLOR_TAG, 0);
    }

    @Override
    default void setLaserColor(ItemStack attachmentStack, int color) {
        attachmentStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> {
            setLaserColorToTag(tag, color);
        }));
    }
}