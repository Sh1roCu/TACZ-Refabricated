package cn.sh1rocu.tacz.util.forge;

import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class StrictNBTIngredient implements CustomIngredient {
    private final ItemStack stack;

    protected StrictNBTIngredient(ItemStack stack) {
        this.stack = stack;
    }

    /**
     * Creates a new ingredient matching the given stack and tag
     */
    public static StrictNBTIngredient of(ItemStack stack) {
        return new StrictNBTIngredient(stack);
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && areShareTagsEqual(this.stack, input);
    }

    private static boolean areShareTagsEqual(ItemStack stack, ItemStack other) {
        CompoundTag shareTagA = stack.getTag();
        CompoundTag shareTagB = other.getTag();
        if (shareTagA == null)
            return shareTagB == null;
        else
            return shareTagB != null && shareTagA.equals(shareTagB);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return Stream.of(stack).toList();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<StrictNBTIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static final ResourceLocation ID = new ResourceLocation("forge", "nbt");

    public static class Serializer implements CustomIngredientSerializer<StrictNBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ResourceLocation getIdentifier() {
            return ID;
        }

        @Override
        public StrictNBTIngredient read(JsonObject json) {
            return new StrictNBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(JsonObject json, StrictNBTIngredient ingredient) {
            json.addProperty("type", ID.toString());
            json.addProperty("item", BuiltInRegistries.ITEM.getKey(ingredient.stack.getItem()).toString());
            json.addProperty("count", ingredient.stack.getCount());
            if (ingredient.stack.hasTag())
                json.addProperty("nbt", ingredient.stack.getTag().toString());
        }

        @Override
        public StrictNBTIngredient read(FriendlyByteBuf buffer) {
            return new StrictNBTIngredient(buffer.readItem());
        }

        @Override
        public void write(FriendlyByteBuf buffer, StrictNBTIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
        }
    }
}
