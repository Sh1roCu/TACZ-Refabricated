package com.tacz.guns.compat.jrei.common;

import com.tacz.guns.crafting.AmmoStorageUpgradeRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;

public interface JREIConstants {
    int MAX_GUN_SHOW_COUNT = 60;

    /**
     * {@link AmmoStorageUpgradeRecipe} 继承自 {@link net.minecraft.world.item.crafting.CustomRecipe}，
     * 其 isSpecial() 返回 true。JEI 无法处理特殊配方（不会读取其布局与产物，导致无法通过物品检索到配方）。
     * 这里参照原版 JEI 内置插件对特殊配方的处理方式，将其展开为一个等价的、JEI 能够识别的 {@link ShapedRecipe}。
     */
    static RecipeHolder<CraftingRecipe> expandToShapedRecipe(RecipeHolder<CraftingRecipe> holder) {
        AmmoStorageUpgradeRecipe recipe = (AmmoStorageUpgradeRecipe) holder.value();
        RegistryAccess registryAccess = Minecraft.getInstance().level.registryAccess();
        var pattern = new net.minecraft.world.item.crafting.ShapedRecipePattern(recipe.getRecipeWidth(),
                recipe.getRecipeHeight(), recipe.getIngredients(), java.util.Optional.empty());
        return new RecipeHolder<>(holder.id(), new ShapedRecipe("", CraftingBookCategory.MISC, pattern,
                recipe.getResultItem(registryAccess)));
    }
}
