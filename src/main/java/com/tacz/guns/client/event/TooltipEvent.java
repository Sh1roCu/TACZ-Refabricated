package com.tacz.guns.client.event;

import com.tacz.guns.api.item.nbt.AmmoItemDataAccessor;
import com.tacz.guns.api.item.nbt.AttachmentItemDataAccessor;
import com.tacz.guns.api.item.nbt.BlockItemDataAccessor;
import com.tacz.guns.api.item.nbt.GunItemDataAccessor;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class TooltipEvent {
    public static void onTooltip(ItemStack stack, TooltipFlag context, List<Component> lines) {
        if (context.isAdvanced() && RenderConfig.ENABLE_TACZ_ID_IN_TOOLTIP.get()) {
            if (stack.getItem() instanceof GunItemDataAccessor item) {
                lines.add(formatTooltip(GunItemDataAccessor.GUN_ID_TAG, item.getGunId(stack)));
            } else if (stack.getItem() instanceof AmmoItemDataAccessor item) {
                lines.add(formatTooltip(AmmoItemDataAccessor.AMMO_ID_TAG, item.getAmmoId(stack)));
            } else if (stack.getItem() instanceof AttachmentItemDataAccessor item) {
                lines.add(formatTooltip(AttachmentItemDataAccessor.ATTACHMENT_ID_TAG, item.getAttachmentId(stack)));
            } else if (stack.getItem() instanceof BlockItemDataAccessor item && !ModItems.GUN_SMITH_TABLE.equals(item)) {
                lines.add(formatTooltip(BlockItemDataAccessor.BLOCK_ID, item.getBlockId(stack)));
            }
        }
    }

    public static Component formatTooltip(String key, ResourceLocation value) {
        return Component.literal(String.format("%s: \"%s\"", key, value)).withStyle(ChatFormatting.DARK_GRAY);
    }
}
