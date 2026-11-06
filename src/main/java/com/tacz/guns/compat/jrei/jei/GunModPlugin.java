package com.tacz.guns.compat.jrei.jei;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.BlockItemBuilder;
import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.compat.jrei.common.JREIConstants;
import com.tacz.guns.compat.jrei.jei.category.*;
import com.tacz.guns.compat.jrei.common.entry.AmmoQueryEntry;
import com.tacz.guns.compat.jrei.common.entry.AttachmentQueryData;
import com.tacz.guns.compat.jrei.common.entry.GunAmmoQueryEntry;
import com.tacz.guns.compat.jrei.jei.transfer.GunSmithTableTransferHandler;
import com.tacz.guns.crafting.AmmoStorageUpgradeRecipe;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModItems;
import com.tacz.guns.init.ModRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@JeiPlugin
public class GunModPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(GunMod.MOD_ID, "jei");

    private Map<ResourceLocation, RecipeType<GunSmithTableRecipe>> recipeTypeMap = new HashMap<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        recipeTypeMap.clear();
        var map = TimelessAPI.getAllCommonBlockIndex();
        for (var entry : map) {
            BlockItem item = entry.getValue().getBlock();
            ItemStack icon = BlockItemBuilder.create(item).setId(entry.getKey()).build();
            RecipeType<GunSmithTableRecipe> type = RecipeType.create(GunMod.MOD_ID, "gun_smith_table/" + entry.getKey().toString().replace(':', '_'), GunSmithTableRecipe.class);
            registration.addRecipeCategories(new GunSmithTableCategory(registration.getJeiHelpers().getGuiHelper(), icon, type, item.getName(icon)));
            recipeTypeMap.put(entry.getKey(), type);
        }
        registration.addRecipeCategories(new AttachmentQueryCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GunAttachmentQueryCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new GunAmmoQueryCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new AmmoQueryCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<GunSmithTableRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipe.GUN_SMITH_TABLE_CRAFTING);

        long workbenchStart = System.nanoTime();
        for (var entry : recipeTypeMap.entrySet()) {
            TimelessAPI.getCommonBlockIndex(entry.getKey()).ifPresent(blockIndex -> {
                List<GunSmithTableRecipe> recipeList = blockIndex.getFilter().filter(recipes, GunSmithTableRecipe::getId);
                recipeList.removeIf(recipe -> {
                    return blockIndex.getData().getTabs().stream().noneMatch(tab -> Objects.equals(tab.id(), recipe.getResult().getGroup()));
                });
                registration.addRecipes(entry.getValue(), recipeList);
            });
        }

        List<CraftingRecipe> ammoStorageUpgradeRecipes = recipeManager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.CRAFTING).stream()
                .filter(AmmoStorageUpgradeRecipe.class::isInstance)
                .map(recipe -> JREIConstants.expandToShapedRecipe((AmmoStorageUpgradeRecipe) recipe))
                .toList();
        registration.addRecipes(RecipeTypes.CRAFTING, ammoStorageUpgradeRecipes);
        long workbenchNanos = System.nanoTime() - workbenchStart;

        long attachmentStart = System.nanoTime();
        AttachmentQueryData attachmentQueries = AttachmentQueryData.load();
        long attachmentBuildNanos = System.nanoTime() - attachmentStart;
        long attachmentRegisterNanos = registerTimed(registration, AttachmentQueryCategory.ATTACHMENT_QUERY, attachmentQueries.attachmentQueries());
        long gunAttachmentRegisterNanos = registerTimed(registration, GunAttachmentQueryCategory.GUN_ATTACHMENT_QUERY, attachmentQueries.gunAttachmentQueries());

        long gunAmmoStart = System.nanoTime();
        List<GunAmmoQueryEntry> gunAmmoEntries = GunAmmoQueryEntry.getAllGunAmmoQueryEntries();
        long gunAmmoBuildNanos = System.nanoTime() - gunAmmoStart;
        long gunAmmoRegisterNanos = registerTimed(registration, GunAmmoQueryCategory.GUN_AMMO_QUERY, gunAmmoEntries);
        long ammoStart = System.nanoTime();
        List<AmmoQueryEntry> ammoEntries = AmmoQueryEntry.getAllAmmoQueryEntries(gunAmmoEntries);
        long ammoBuildNanos = System.nanoTime() - ammoStart;
        long ammoRegisterNanos = registerTimed(registration, AmmoQueryCategory.AMMO_QUERY, ammoEntries);

        GunMod.LOGGER.info("JEI query timings (ms): workbench/crafting={}, attachment build={} ({} guns, {} attachments, {} checks), " +
                        "attachment register={}, gun-attachment register={}, gun-ammo build={}, gun-ammo register={}, ammo build={}, ammo register={}",
                TimeUnit.NANOSECONDS.toMillis(workbenchNanos), TimeUnit.NANOSECONDS.toMillis(attachmentBuildNanos),
                attachmentQueries.gunCount(), attachmentQueries.attachmentCount(), attachmentQueries.compatibilityChecks(),
                TimeUnit.NANOSECONDS.toMillis(attachmentRegisterNanos), TimeUnit.NANOSECONDS.toMillis(gunAttachmentRegisterNanos),
                TimeUnit.NANOSECONDS.toMillis(gunAmmoBuildNanos), TimeUnit.NANOSECONDS.toMillis(gunAmmoRegisterNanos),
                TimeUnit.NANOSECONDS.toMillis(ammoBuildNanos), TimeUnit.NANOSECONDS.toMillis(ammoRegisterNanos));
    }

    private static <T> long registerTimed(IRecipeRegistration registration, RecipeType<T> type, List<T> recipes) {
        long start = System.nanoTime();
        registration.addRecipes(type, recipes);
        return System.nanoTime() - start;
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
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        for (RecipeType<GunSmithTableRecipe> type : recipeTypeMap.values()) {
            registration.addRecipeTransferHandler(new GunSmithTableTransferHandler(type, registration.getTransferHelper()), type);
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
    public ResourceLocation getPluginUid() {
        return UID;
    }
}
