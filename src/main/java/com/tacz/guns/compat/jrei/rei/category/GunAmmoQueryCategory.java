package com.tacz.guns.compat.jrei.rei.category;

import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import com.tacz.guns.compat.jrei.rei.display.GunAmmoQueryDisplay;
import com.tacz.guns.init.ModCreativeTabs;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class GunAmmoQueryCategory implements DisplayCategory<GunAmmoQueryDisplay> {
    private static final Component TITLE = Component.translatable("jei.tacz.gun_ammo_query.title");
    private static final Component AMMO_LABEL = Component.translatable("jei.tacz.gun_ammo_query.ammo");
    private static final int WIDTH = 162;
    private final Renderer icon;

    public GunAmmoQueryCategory() {
        this.icon = EntryStacks.of(ModCreativeTabs.GUN_RIFLE_TAB.getIconItem());
    }

    @Override
    public List<Widget> setupDisplay(GunAmmoQueryDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int startX = bounds.x;
        int startY = bounds.y;

        // 枪械作为输出，从其 REI 配方页查询所用弹药。
        widgets.add(Widgets.createSlot(new Point(startX + 73, startY + 5)).entries(display.getOutputEntries().get(0)).markOutput());
        widgets.add(Widgets.createSlot(new Point(startX + 73, startY + 35)).entries(display.getInputEntries().get(0)).markInput());

        Font font = Minecraft.getInstance().font;
        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            guiGraphics.drawString(font, AMMO_LABEL, startX + (WIDTH - font.width(AMMO_LABEL)) / 2, startY + 24, 0x555555, false);
        }));

        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends GunAmmoQueryDisplay> getCategoryIdentifier() {
        return GunModREIClientPlugin.GUN_AMMO_QUERY;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public int getDisplayWidth(GunAmmoQueryDisplay display) {
        return WIDTH;
    }
}
