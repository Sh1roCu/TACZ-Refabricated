package com.tacz.guns.resource.pojo.data.loot;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.ArrayList;
import java.util.List;

public record LootTableInjection(List<ResourceLocation> lootTables, LootTable lootTable, ResourceLocation id) {
    private static final Gson LOOT_TABLE_GSON = Deserializers.createLootTableSerializer().create();

    public static LootTableInjection fromJson(ResourceLocation fileId, JsonElement element) {
        JsonObject object = GsonHelper.convertToJsonObject(element, "loot injection");
        List<ResourceLocation> lootTables = readLootTables(fileId, object);
        if (!object.has("pools")) {
            throw new JsonParseException("Loot injection " + fileId + " must define pools");
        }

        var lootTable = LOOT_TABLE_GSON.fromJson(object, LootTable.class);
        return new LootTableInjection(lootTables, lootTable, fileId);
    }

    private static List<ResourceLocation> readLootTables(ResourceLocation fileId, JsonObject object) {
        List<ResourceLocation> lootTables = new ArrayList<>();
        if (object.has("loot_tables")) {
            for (JsonElement table : GsonHelper.getAsJsonArray(object, "loot_tables")) {
                lootTables.add(new ResourceLocation(GsonHelper.convertToString(table, "loot table")));
            }
        } else if (object.has("loot_table")) {
            lootTables.add(new ResourceLocation(GsonHelper.getAsString(object, "loot_table")));
        } else {
            throw new JsonParseException("Loot injection " + fileId + " must define loot_table or loot_tables");
        }
        return List.copyOf(lootTables);
    }

    public List<ItemStack> createStacks(LootContext context) {
        List<ItemStack> stacks = new ArrayList<>();
        lootTable.getRandomItemsRaw(context, stacks::add);
        return stacks;
    }
}