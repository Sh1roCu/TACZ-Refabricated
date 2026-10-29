package com.tacz.guns.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.attachment.AttachmentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

@SuppressWarnings("deprecation")
public final class VirtualOemAttachment {
    private static final String VIRTUAL_OEM_TAG = "VirtualOemAttachment";

    private VirtualOemAttachment() {
    }

    public static boolean isAllowedVirtualOem(ResourceLocation gunId, ResourceLocation attachmentId, AttachmentType type) {
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> {
                    var builtInAttachment = index.getGunData().getBuiltInAttachments().get(type);
                    return builtInAttachment != null && builtInAttachment.getAttachments().contains(attachmentId);
                })
                .orElse(false);
    }

    public static boolean shouldShowIcon(ResourceLocation gunId, ResourceLocation attachmentId, AttachmentType type) {
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> {
                    var builtInAttachment = index.getGunData().getBuiltInAttachments().get(type);
                    // 回退显示的配件已在枪上，不重复列入改装选项。
                    return builtInAttachment != null && builtInAttachment.getAttachments().contains(attachmentId)
                            && builtInAttachment.isShowIcon() && !attachmentId.equals(builtInAttachment.getDisplayAttachment());
                })
                .orElse(false);
    }

    public static boolean isBuiltinAttachment(ResourceLocation attachmentId) {
        return TimelessAPI.getCommonAttachmentIndex(attachmentId)
                .map(index -> index.getPojo().isBuiltinAttachment())
                .orElse(false);
    }

    public static void mark(ItemStack attachmentStack) {
        attachmentStack.update(DataComponents.CUSTOM_DATA, CustomData.EMPTY, data -> data.update(tag -> tag.putBoolean(VIRTUAL_OEM_TAG, true)));
    }

    public static boolean isMarked(ItemStack attachmentStack) {
        CompoundTag tag = attachmentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getUnsafe();
        return tag != null && tag.contains(VIRTUAL_OEM_TAG, Tag.TAG_BYTE) && tag.getBoolean(VIRTUAL_OEM_TAG);
    }
}