package com.tacz.guns.compat.jrei.rei;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.BlockItemBuilder;
import com.tacz.guns.compat.jrei.common.JREIConstants;
import com.tacz.guns.compat.jrei.common.entry.AmmoQueryEntry;
import com.tacz.guns.compat.jrei.common.entry.AttachmentQueryData;
import com.tacz.guns.compat.jrei.common.entry.GunAmmoQueryEntry;
import com.tacz.guns.compat.jrei.rei.category.*;
import com.tacz.guns.compat.jrei.rei.display.*;
import com.tacz.guns.compat.jrei.rei.transfer.GunSmithTableTransferHandler;
import com.tacz.guns.crafting.AmmoStorageUpgradeRecipe;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModRecipe;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class GunModREIClientPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<AttachmentQueryDisplay> ATTACHMENT_QUERY = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/attachment_query");
    public static final CategoryIdentifier<GunAttachmentQueryDisplay> GUN_ATTACHMENT_QUERY = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/gun_attachment_query");
    public static final CategoryIdentifier<GunAmmoQueryDisplay> GUN_AMMO_QUERY = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/gun_ammo_query");
    public static final CategoryIdentifier<AmmoQueryDisplay> AMMO_QUERY = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/ammo_query");

    public static final Map<ResourceLocation, CategoryIdentifier<GunSmithTableDisplay>> displays = new HashMap<>();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        displays.clear();
        var map = TimelessAPI.getAllCommonBlockIndex();
        for (var entry : map) {
            BlockItem item = entry.getValue().getBlock();
            ItemStack icon = BlockItemBuilder.create(item).setId(entry.getKey()).build();
            CategoryIdentifier<GunSmithTableDisplay> id = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/gun_smith_table/" + entry.getKey().toString().replace(':', '_'));
            registry.add(new GunSmithTableCategory(item.getName(icon), icon, id));
            registry.addWorkstations(id, EntryStacks.of(icon));
            displays.put(entry.getKey(), id);
        }
        registry.add(new AttachmentQueryCategory());
        registry.add(new GunAttachmentQueryCategory());
        registry.add(new GunAmmoQueryCategory());
        registry.add(new AmmoQueryCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (Minecraft.getInstance().level == null) return;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<GunSmithTableRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipe.GUN_SMITH_TABLE_CRAFTING);

        long workbenchStart = System.nanoTime();
        for (var entry : displays.entrySet()) {
            TimelessAPI.getCommonBlockIndex(entry.getKey()).ifPresent(blockIndex -> {
                blockIndex.getFilter().filter(recipes, GunSmithTableRecipe::getId)
                        .stream()
                        .filter(recipe ->
                                blockIndex.getData().getTabs().stream().anyMatch(tab -> Objects.equals(tab.id(), recipe.getResult().getGroup())))
                        .toList().forEach(recipe -> registry.add(new GunSmithTableDisplay(recipe, entry)));
            });
        }

        recipeManager.getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(AmmoStorageUpgradeRecipe.class::isInstance)
                .map(recipe -> JREIConstants.expandToShapedRecipe((AmmoStorageUpgradeRecipe) recipe))
                .toList()
                .forEach(recipe -> registry.add(DefaultCraftingDisplay.of(recipe)));
        long workbenchNanos = System.nanoTime() - workbenchStart;

        long attachmentStart = System.nanoTime();
        AttachmentQueryData attachmentQueries = AttachmentQueryData.load();
        long attachmentBuildNanos = System.nanoTime() - attachmentStart;

        long attachmentRegisterNanos = registerTimed(() -> attachmentQueries.attachmentQueries().forEach(entry ->
                registry.add(new AttachmentQueryDisplay(entry))));
        long gunAttachmentRegisterNanos = registerTimed(() -> attachmentQueries.gunAttachmentQueries().forEach(entry ->
                registry.add(new GunAttachmentQueryDisplay(entry))));

        long gunAmmoStart = System.nanoTime();
        List<GunAmmoQueryEntry> gunAmmoEntries = GunAmmoQueryEntry.getAllGunAmmoQueryEntries();
        long gunAmmoBuildNanos = System.nanoTime() - gunAmmoStart;
        long gunAmmoRegisterNanos = registerTimed(() -> gunAmmoEntries.forEach(entry ->
                registry.add(new GunAmmoQueryDisplay(entry))));
        long ammoStart = System.nanoTime();
        List<AmmoQueryEntry> ammoEntries = AmmoQueryEntry.getAllAmmoQueryEntries(gunAmmoEntries);
        long ammoBuildNanos = System.nanoTime() - ammoStart;
        long ammoRegisterNanos = registerTimed(() -> ammoEntries.forEach(entry ->
                registry.add(new AmmoQueryDisplay(entry))));

        GunMod.LOGGER.info("REI query timings (ms): workbench/crafting={}, attachment build={} ({} guns, {} attachments, {} checks), " +
                        "attachment register={}, gun-attachment register={}, gun-ammo build={}, gun-ammo register={}, ammo build={}, ammo register={}",
                TimeUnit.NANOSECONDS.toMillis(workbenchNanos), TimeUnit.NANOSECONDS.toMillis(attachmentBuildNanos),
                attachmentQueries.gunCount(), attachmentQueries.attachmentCount(), attachmentQueries.compatibilityChecks(),
                TimeUnit.NANOSECONDS.toMillis(attachmentRegisterNanos), TimeUnit.NANOSECONDS.toMillis(gunAttachmentRegisterNanos),
                TimeUnit.NANOSECONDS.toMillis(gunAmmoBuildNanos), TimeUnit.NANOSECONDS.toMillis(gunAmmoRegisterNanos),
                TimeUnit.NANOSECONDS.toMillis(ammoBuildNanos), TimeUnit.NANOSECONDS.toMillis(ammoRegisterNanos));
    }

    private static long registerTimed(Runnable runnable) {
        long start = System.nanoTime();
        runnable.run();
        return System.nanoTime() - start;
    }

    @Override
    public void registerTransferHandlers(TransferHandlerRegistry registry) {
        registry.register(new GunSmithTableTransferHandler());
    }
}
