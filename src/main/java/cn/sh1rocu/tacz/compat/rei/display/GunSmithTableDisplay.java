package cn.sh1rocu.tacz.compat.rei.display;

import com.tacz.guns.crafting.GunSmithTableRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.Identifier;

import me.shedaniel.rei.api.common.display.DisplaySerializer;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class GunSmithTableDisplay extends BasicDisplay {
    private final GunSmithTableRecipe recipe;
    private final Map.Entry<Identifier, CategoryIdentifier<GunSmithTableDisplay>> entry;

    public GunSmithTableDisplay(GunSmithTableRecipe recipe, Map.Entry<Identifier, CategoryIdentifier<GunSmithTableDisplay>> entry) {
        super(recipe.getInputs().stream().map(i -> EntryIngredients.ofIngredient(i.getIngredient())).collect(Collectors.toList()), Collections.singletonList(EntryIngredients.of(recipe.getOutput())), Optional.ofNullable(entry.getKey()));
        this.recipe = recipe;
        this.entry = entry;
    }

    public GunSmithTableRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return entry.getValue();
    }

    @Override
    public DisplaySerializer<? extends me.shedaniel.rei.api.common.display.Display> getSerializer() {
        throw new UnsupportedOperationException("GunSmithTableDisplay does not support serialization");
    }
}