package com.tacz.guns.loot;

import cn.sh1rocu.tacz.mixin.common.accessor.LootManagerAccessor;
import com.tacz.guns.resource.CommonAssetsManager;
import com.tacz.guns.resource.pojo.data.loot.LootTableInjection;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootTableInjectorModifier {
    private static final Map<LootTable, ResourceLocation> ID_CACHE = new HashMap<>();

    public static @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context, LootTable table) {
        CommonAssetsManager manager = CommonAssetsManager.getInstance();
        if (manager == null) {
            return generatedLoot;
        }

        ResourceLocation lootTableId = ID_CACHE.computeIfAbsent(
                table, lootTable -> ((LootManagerAccessor) context.getLevel().getServer().getLootData()).tacz$elements()
                        .entrySet()
                        .stream()
                        .filter(entry -> lootTable.equals(entry.getValue()))
                        .map(key -> key.getKey().location())
                        .findFirst()
                        .orElse(null)
        );
        if (lootTableId == null) {
            return generatedLoot;
        }

        List<LootTableInjection> injections = manager.getLootTableInjections(lootTableId);
        if (injections.isEmpty()) {
            return generatedLoot;
        }

        for (LootTableInjection injection : injections) {
            generatedLoot.addAll(injection.createStacks(context));
        }
        return generatedLoot;
    }
}