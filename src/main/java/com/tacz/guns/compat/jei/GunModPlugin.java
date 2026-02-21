package com.tacz.guns.compat.jei;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.BlockItemBuilder;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.compat.jei.category.AttachmentQueryCategory;
import com.tacz.guns.compat.jei.category.GunSmithTableCategory;
import com.tacz.guns.compat.jei.entry.AttachmentQueryEntry;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.init.ModRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class GunModPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "jei");

    private Map<Identifier, RecipeType<GunSmithTableRecipe>> recipeTypeMap = new HashMap<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        recipeTypeMap.clear();
        var map = TimelessAPI.getAllCommonBlockIndex();
        for (var entry : map) {
            BlockItem item = entry.getValue().getBlock();
            ItemStack icon = BlockItemBuilder.create(item).setId(entry.getKey()).build();
            RecipeType<GunSmithTableRecipe> type = RecipeType.create(GunMod.MOD_ID, "gun_smith_table/" + entry.getKey().toString().replace(':', '_'), GunSmithTableRecipe.class);
            registration.addRecipeCategories(new GunSmithTableCategory(registration.getJeiHelpers().getGuiHelper(), icon, type, Component.translatable(entry.getValue().getPojo().getName())));
            recipeTypeMap.put(entry.getKey(), type);
        }
        registration.addRecipeCategories(new AttachmentQueryCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        List<RecipeHolder<GunSmithTableRecipe>> recipes = new java.util.ArrayList<>(Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes().getAllOfType(ModRecipe.GUN_SMITH_TABLE_CRAFTING));

        for (var entry : recipeTypeMap.entrySet()) {
            TimelessAPI.getCommonBlockIndex(entry.getKey()).ifPresent(blockIndex -> {
                List<GunSmithTableRecipe> recipeList = blockIndex.getFilter().filter(recipes, h -> h.id().identifier()).stream().map(RecipeHolder::value).collect(Collectors.toList());
                recipeList.removeIf(recipe -> blockIndex.getData().getTabs().stream().noneMatch(tab -> Objects.equals(tab.id(), recipe.getResult().getGroup())));
                registration.addRecipes(entry.getValue(), recipeList);
            });
        }

        registration.addRecipes(AttachmentQueryCategory.ATTACHMENT_QUERY, AttachmentQueryEntry.getAllAttachmentQueryEntries());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (var entry : recipeTypeMap.entrySet()) {
            TimelessAPI.getCommonBlockIndex(entry.getKey()).ifPresent(blockIndex -> {
                ItemStack stack = BlockItemBuilder.create(blockIndex.getBlock()).setId(entry.getKey()).build();
                registration.addRecipeCatalyst(stack, entry.getValue());
            });

        }

    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AMMO, GunModSubtype.getAmmoSubtype());
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.ATTACHMENT, GunModSubtype.getAttachmentSubtype());
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.AMMO_BOX, GunModSubtype.getAmmoBoxSubtype());
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.WORKBENCH_111, GunModSubtype.getTableSubType());
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.WORKBENCH_121, GunModSubtype.getTableSubType());
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.WORKBENCH_211, GunModSubtype.getTableSubType());
        GunItemManager.getAllGunItems().forEach(item -> registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, GunModSubtype.getGunSubtype()));
    }

    @Override
    public Identifier getPluginUid() {
        return UID;
    }
}