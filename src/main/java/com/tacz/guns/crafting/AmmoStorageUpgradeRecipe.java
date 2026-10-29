package com.tacz.guns.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.init.ModRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public class AmmoStorageUpgradeRecipe extends CustomRecipe {
    private final boolean diamond;
    private final Item from;
    private final Item material;
    private final Item to;

    public AmmoStorageUpgradeRecipe(CraftingBookCategory category, boolean diamond) {
        super(category);
        this.diamond = diamond;
        if (diamond) {
            from = ModItems.AMMO_STORAGE_GOLD;
            material = Items.DIAMOND;
            to = ModItems.AMMO_STORAGE_DIAMOND;
        } else {
            from = ModItems.AMMO_STORAGE;
            material = Items.GOLD_INGOT;
            to = ModItems.AMMO_STORAGE_GOLD;
        }
    }

    @Override
    public boolean matches(CraftingInput container, Level level) {
        if (container.width() != 3 || container.height() != 3) {
            return false;
        }
        for (int slot = 0; slot < container.size(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (slot == 4) {
                if (!stack.is(from)) {
                    return false;
                }
            } else if (!stack.is(material)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput container, net.minecraft.core.HolderLookup.Provider registryAccess) {
        ItemStack result = getResultItem(registryAccess);
        var blockEntityData = container.getItem(4).get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
        if (blockEntityData != null)
            result.set(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA, blockEntityData);
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(9, Ingredient.EMPTY);
        Ingredient materialIngredient = Ingredient.of(material);
        for (int slot = 0; slot < ingredients.size(); slot++) {
            ingredients.set(slot, slot == 4 ? Ingredient.of(from) : materialIngredient);
        }
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(net.minecraft.core.HolderLookup.Provider registryAccess) {
        return new ItemStack(to);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    public int getRecipeWidth() {
        return 3;
    }

    public int getRecipeHeight() {
        return 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipe.AMMO_STORAGE_UPGRADE_RECIPE_SERIALIZER;
    }

    public static class Serializer implements RecipeSerializer<AmmoStorageUpgradeRecipe> {
        private static final MapCodec<AmmoStorageUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(AmmoStorageUpgradeRecipe::category),
                Codec.BOOL.optionalFieldOf("diamond", false).forGetter(recipe -> recipe.diamond)
        ).apply(instance, AmmoStorageUpgradeRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, AmmoStorageUpgradeRecipe> STREAM_CODEC = StreamCodec.of(
                (buffer, recipe) -> {
                    buffer.writeEnum(recipe.category());
                    buffer.writeBoolean(recipe.diamond);
                },
                buffer -> new AmmoStorageUpgradeRecipe(buffer.readEnum(CraftingBookCategory.class), buffer.readBoolean()));

        @Override
        public MapCodec<AmmoStorageUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AmmoStorageUpgradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
