package cn.sh1rocu.tacz.compat.rei;

import cn.sh1rocu.tacz.compat.rei.category.AttachmentQueryCategory;
import cn.sh1rocu.tacz.compat.rei.category.GunSmithTableCategory;
import cn.sh1rocu.tacz.compat.rei.display.AttachmentQueryDisplay;
import cn.sh1rocu.tacz.compat.rei.display.GunSmithTableDisplay;
import cn.sh1rocu.tacz.compat.rei.entry.AttachmentQueryEntry;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.builder.BlockItemBuilder;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import com.tacz.guns.init.ModRecipe;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
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

public class REIClientPlugin implements me.shedaniel.rei.api.client.plugins.REIClientPlugin {
    public static final CategoryIdentifier<AttachmentQueryDisplay> ATTACHMENT_QUERY = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/attachment_query");

    public static final Map<Identifier, CategoryIdentifier<GunSmithTableDisplay>> displays = new HashMap<>();

    @Override
    public void registerCategories(CategoryRegistry registry) {
        var map = TimelessAPI.getAllCommonBlockIndex();
        for (var entry : map) {
            BlockItem item = entry.getValue().getBlock();
            ItemStack icon = BlockItemBuilder.create(item).setId(entry.getKey()).build();
            // 根据需要的枪械工作台类型生成动态id
            CategoryIdentifier<GunSmithTableDisplay> id = CategoryIdentifier.of(GunMod.MOD_ID, "plugins/gun_smith_table/" + entry.getKey().toString().replace(':', '_'));
            registry.add(new GunSmithTableCategory(Component.translatable(entry.getValue().getPojo().getName()), icon, id));
            displays.put(entry.getKey(), id);
            registry.addWorkstations(id, EntryStacks.of(icon));
        }
        registry.add(new AttachmentQueryCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (Minecraft.getInstance().level == null) return;
        List<RecipeHolder<GunSmithTableRecipe>> recipes = new java.util.ArrayList<>(Minecraft.getInstance().level.recipeAccess().getSynchronizedRecipes().getAllOfType(ModRecipe.GUN_SMITH_TABLE_CRAFTING));

        for (var entry : displays.entrySet()) {
            TimelessAPI.getCommonBlockIndex(entry.getKey()).ifPresent(blockIndex -> {
                List<GunSmithTableRecipe> recipeList = blockIndex.getFilter().filter(recipes, h -> h.id().identifier()).stream().map(RecipeHolder::value).collect(Collectors.toList());
                recipeList.removeIf(recipe -> blockIndex.getData().getTabs().stream().noneMatch(tab -> Objects.equals(tab.id(), recipe.getResult().getGroup())));
                recipeList.forEach(recipe -> registry.add(new GunSmithTableDisplay(recipe, entry)));
            });
        }

        AttachmentQueryEntry.getAllAttachmentQueryEntries().forEach(entry ->
                registry.add(new AttachmentQueryDisplay(entry)));
    }
}
