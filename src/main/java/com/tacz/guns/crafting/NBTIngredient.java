package com.tacz.guns.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.GunMod;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NBTIngredient implements CustomIngredient {
    public static final Codec<HolderSet<Item>> ITEM_HOLDER_SET_CODEC = HolderSetCodec.create(Registries.ITEM, BuiltInRegistries.ITEM.holderByNameCodec(), false);
    public static final MapCodec<NBTIngredient> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            ITEM_HOLDER_SET_CODEC.fieldOf("items").forGetter(NBTIngredient::items),
            TagParser.LENIENT_CODEC.fieldOf("nbt").forGetter(NBTIngredient::nbt),
            Codec.BOOL.optionalFieldOf("partial", true).forGetter(NBTIngredient::partial)
    ).apply(builder, NBTIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, NBTIngredient> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodecWithRegistries(ITEM_HOLDER_SET_CODEC),
            NBTIngredient::items,
            ByteBufCodecs.COMPOUND_TAG,
            NBTIngredient::nbt,
            ByteBufCodecs.BOOL,
            NBTIngredient::partial,
            NBTIngredient::new
    );

    private final HolderSet<Item> items;
    private final CompoundTag nbt;
    private final NbtPredicate predicate;
    private final boolean partial;
    private final ItemStack[] stacks;

    public NBTIngredient(HolderSet<Item> items, CompoundTag nbt, boolean partial) {
        this.items = items;
        this.nbt = nbt;
        this.predicate = new NbtPredicate(nbt);
        this.partial = partial;
        DataComponentPatch patch = DataComponentPatch.builder()
                .set(DataComponents.CUSTOM_DATA, CustomData.of(nbt))
                .build();
        this.stacks = items.stream()
                .map(i -> new ItemStack(i, 1, patch))
                .toArray(ItemStack[]::new);
    }

    @Override
    public boolean test(@Nullable ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!this.items.contains(itemStack.getItemHolder())) return false;
        CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbt = data != null ? data.copyTag() : null;
        if (this.partial) {
            return predicate.matches(nbt);
        } else {
            return Objects.equals(nbt, this.nbt);
        }
    }

    @Override
    public java.util.stream.Stream<net.minecraft.core.Holder<Item>> getMatchingItems() {
        return this.items.stream();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public HolderSet<Item> items() {
        return items;
    }

    public CompoundTag nbt() {
        return nbt;
    }

    public boolean partial() {
        return partial;
    }

    public static final Identifier ID = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "nbt");

    public static class Serializer implements CustomIngredientSerializer<NBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public Identifier getIdentifier() {
            return ID;
        }

        @Override
        public MapCodec<NBTIngredient> getCodec() {
            return NBTIngredient.CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, NBTIngredient> getPacketCodec() {
            return NBTIngredient.STREAM_CODEC;
        }


    }
}