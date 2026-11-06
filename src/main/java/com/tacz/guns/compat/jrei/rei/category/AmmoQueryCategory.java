package com.tacz.guns.compat.jrei.rei.category;

import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import com.tacz.guns.compat.jrei.rei.display.AmmoQueryDisplay;
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

public class AmmoQueryCategory implements DisplayCategory<AmmoQueryDisplay> {
    private static final Component TITLE = Component.translatable("jei.tacz.ammo_query.title");
    private static final int WIDTH = 162;
    private static final int COLUMNS = 9;
    private final Renderer icon;

    public AmmoQueryCategory() {
        this.icon = EntryStacks.of(ModCreativeTabs.AMMO_TAB.getIconItem());
    }

    @Override
    public List<Widget> setupDisplay(AmmoQueryDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        var entry = display.getEntry();

        int startX = bounds.x;
        int startY = bounds.y;

        // 弹药作为输出，从其 REI 配方页查询全部适用枪械。
        widgets.add(Widgets.createSlot(new Point(startX + 73, startY + 1)).entries(display.getOutputEntries().get(0)).markOutput());
        for (int i = 0; i < entry.getGunStacks().size(); i++) {
            int x = 1 + i % COLUMNS * 18;
            int y = 35 + i / COLUMNS * 18;
            widgets.add(Widgets.createSlot(new Point(startX + x, startY + y)).entries(display.getInputEntries().get(i)).markInput());
        }

        Font font = Minecraft.getInstance().font;
        Component gunsLabel = Component.translatable("jei.tacz.ammo_query.guns", entry.getTotalGunCount());
        widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            guiGraphics.drawString(font, gunsLabel, startX + (WIDTH - font.width(gunsLabel)) / 2, startY + 22, 0x555555, false);
        }));
        if (entry.getPageCount() > 1) {
            Component page = Component.translatable("jei.tacz.ammo_query.page", entry.getPageNumber(), entry.getPageCount());
            widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
                guiGraphics.drawString(font, page, startX + (WIDTH - font.width(page)) / 2, startY + 145, 0x555555, false);
            }));
        }

        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends AmmoQueryDisplay> getCategoryIdentifier() {
        return GunModREIClientPlugin.AMMO_QUERY;
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
        return 156;
    }

    @Override
    public int getDisplayWidth(AmmoQueryDisplay display) {
        return WIDTH;
    }
}
