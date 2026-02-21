package com.tacz.guns.init;

import com.tacz.guns.GunMod;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.crafting.GunSmithTableSerializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipe {
    public static void init() {

    }

    public static RecipeSerializer<GunSmithTableRecipe> GUN_SMITH_TABLE_RECIPE_SERIALIZER = registerSerializer("gun_smith_table_crafting", new GunSmithTableSerializer());
    public static RecipeType<GunSmithTableRecipe> GUN_SMITH_TABLE_CRAFTING = registerRecipe("gun_smith_table_crafting", new RecipeType<>() {
        @Override
        public String toString() {
            return GunMod.MOD_ID + ":gun_smith_table_crafting";
        }
    });

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String name, S serializer) {
        return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name), serializer);
    }

    private static <T extends Recipe<?>> RecipeType<T> registerRecipe(String name, RecipeType<T> type) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, Identifier.fromNamespaceAndPath(GunMod.MOD_ID, name), type);
    }
}
