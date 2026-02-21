package cn.sh1rocu.tacz.compat.rei.category;

import cn.sh1rocu.tacz.compat.rei.display.GunSmithTableDisplay;
import com.tacz.guns.crafting.GunSmithTableIngredient;
import com.tacz.guns.crafting.GunSmithTableRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GunSmithTableCategory implements DisplayCategory<GunSmithTableDisplay> {
    private final Component title;
    private final Renderer icon;
    private final CategoryIdentifier<GunSmithTableDisplay> id;

    public GunSmithTableCategory(Component title, ItemStack icon, CategoryIdentifier<GunSmithTableDisplay> id) {
        this.title = title;
        this.icon = EntryStacks.of(icon);
        this.id = id;
    }

    @Override
    public List<Widget> setupDisplay(GunSmithTableDisplay display, Rectangle bounds) {
        GunSmithTableRecipe recipe = display.getRecipe();
        List<GunSmithTableIngredient> inputs = recipe.getInputs();
        EntryStack<ItemStack> output = EntryStack.of(VanillaEntryTypes.ITEM, recipe.getOutput());

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int startX = bounds.x + 5;
        int startY = bounds.y;

        widgets.add(Widgets.createSlot(new Point(startX + 3, startY + 12)).entry(output).markOutput());

        int size = inputs.size();
        // 单行排布
        if (size < 7) {
            for (int i = 0; i < size; i++) {
                int xOffset = startX + 35 + 20 * i;
                int yOffset = startY + 12;
                widgets.add(Widgets.createSlot(new Point(xOffset, yOffset)).entries(getInput(inputs, i)).markInput());
            }
        }
        // 双行排布
        else {
            for (int i = 0; i < 6; i++) {
                int xOffset = startX + 35 + 20 * i;
                int yOffset = startY + 2;
                widgets.add(Widgets.createSlot(new Point(xOffset, yOffset)).entries(getInput(inputs, i)).markInput());
            }
            for (int i = 6; i < size; i++) {
                int xOffset = startX + 35 + 20 * (i - 6);
                int yOffset = startY + 22;
                widgets.add(Widgets.createSlot(new Point(xOffset, yOffset)).entries(getInput(inputs, i)).markInput());
            }
        }

        return widgets;
    }

    private List<EntryStack<ItemStack>> getInput(List<GunSmithTableIngredient> inputs, int index) {
        if (index < inputs.size()) {
            GunSmithTableIngredient ingredient = inputs.get(index);
            ItemStack[] items = ingredient.getIngredient().items().map(holder -> new ItemStack(holder)).toArray(ItemStack[]::new);
            return Arrays.stream(items).map(stack -> EntryStack.of(VanillaEntryTypes.ITEM, stack.copyWithCount(ingredient.getCount()))).toList();
        }
        return Collections.singletonList(EntryStack.of(VanillaEntryTypes.ITEM, ItemStack.EMPTY));
    }

    @Override
    public CategoryIdentifier<? extends GunSmithTableDisplay> getCategoryIdentifier() {
        return id;
    }

    @Override
    public Component getTitle() {
        return title;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public int getDisplayWidth(GunSmithTableDisplay display) {
        return 160;
    }
}
