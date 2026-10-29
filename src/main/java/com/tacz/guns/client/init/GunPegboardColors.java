package com.tacz.guns.client.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.block.entity.GunPegboardBlockEntity;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.item.GunPegboardItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

@Environment(EnvType.CLIENT)
public final class GunPegboardColors {
    public static void blockColors() {
        ColorProviderRegistry.BLOCK.register((state, level, pos, tint) -> level != null && pos != null
                && level.getBlockEntity(pos) instanceof GunPegboardBlockEntity pegboard && pegboard.getColor() >= 0
                ? pegboard.getColor() : -1, ModBlocks.GUN_PEGBOARD, ModBlocks.GLOW_GUN_PEGBOARD);
    }

    public static void itemColors() {
        ColorProviderRegistry.ITEM.register((stack, tint) -> GunPegboardItem.hasCustomColor(stack)
                ? FastColor.ARGB32.opaque(GunPegboardItem.getColor(stack)) : -1, ModItems.GUN_PEGBOARD, ModItems.GLOW_GUN_PEGBOARD);
    }

    public static void setup() {
        ItemProperties.register(ModItems.GUN_PEGBOARD, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "dyed"),
                (stack, level, entity, seed) -> GunPegboardItem.hasCustomColor(stack) ? 1 : 0);
        ItemProperties.register(ModItems.GLOW_GUN_PEGBOARD, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "dyed"),
                (stack, level, entity, seed) -> GunPegboardItem.hasCustomColor(stack) ? 1 : 0);
    }
}
