package com.tacz.guns.compat.jrei.rei.display;

import com.tacz.guns.crafting.GunSmithTableRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class GunSmithTableDisplay extends BasicDisplay {
    private final RecipeHolder<GunSmithTableRecipe> recipe;
    private final Map.Entry<ResourceLocation, CategoryIdentifier<GunSmithTableDisplay>> entry;

    public GunSmithTableDisplay(RecipeHolder<GunSmithTableRecipe> recipe, Map.Entry<ResourceLocation, CategoryIdentifier<GunSmithTableDisplay>> entry) {
        super(EntryIngredients.ofIngredients(recipe.value().getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.value().getOutput())), Optional.ofNullable(entry.getKey()));
        this.recipe = recipe;
        this.entry = entry;
    }

    public RecipeHolder<GunSmithTableRecipe> getRecipeHolder() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return entry.getValue();
    }
}