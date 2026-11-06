package com.tacz.guns.compat.jrei.rei.category;

import com.tacz.guns.compat.jrei.common.JREIConstants;
import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import com.tacz.guns.compat.jrei.rei.display.AttachmentQueryDisplay;
import com.tacz.guns.init.ModCreativeTabs;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class AttachmentQueryCategory implements DisplayCategory<AttachmentQueryDisplay> {
    private static final Component TITLE = Component.translatable("jei.tacz.attachment_query.title");
    private final Renderer icon;

    public AttachmentQueryCategory() {
        this.icon = EntryStacks.of(ModCreativeTabs.ATTACHMENT_SCOPE_TAB.getIconItem());
    }

    @Override
    public List<Widget> setupDisplay(AttachmentQueryDisplay display, Rectangle bounds) {
        List<EntryIngredient> inputs = display.getInputEntries();
        List<EntryIngredient> outputs = display.getOutputEntries();

        List<Widget> widgets = new ArrayList<>();

        int startX = bounds.x;
        int startY = bounds.y;

        // 先把配件放在正中央
        widgets.add(Widgets.createSlot(new Point(startX + 72, startY)).entries(outputs.getFirst()).markOutput());

        // 逐行画枪械，每行 9 个
        int xOffset = 0;
        int yOffset = 20;
        for (int i = 0; i < Math.min(inputs.size(), JREIConstants.MAX_GUN_SHOW_COUNT); i++) {
            int column = i % 9;
            int row = i / 9;
            xOffset = column * 18;
            yOffset = 20 + row * 18;
            widgets.add(Widgets.createSlot(new Point(startX + xOffset, startY + yOffset)).entries(inputs.get(i)).markInput());
        }

        // 如果超出上限，那么最后一格则为来回跳变的物品
        if (inputs.size() > JREIConstants.MAX_GUN_SHOW_COUNT) {
            Font font = Minecraft.getInstance().font;
            widgets.add(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) ->
                    guiGraphics.drawString(font, Component.translatable("jei.tacz.attachment_query.more"), startX + 128, startY + 134, 0x555555, false)));
            widgets.add(Widgets.createSlot(new Point(startX + xOffset + 18, startY + yOffset)).entries(inputs.getLast()).markInput());
        }

        return widgets;
    }

    @Override
    public CategoryIdentifier<? extends AttachmentQueryDisplay> getCategoryIdentifier() {
        return GunModREIClientPlugin.ATTACHMENT_QUERY;
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
        return 145;
    }

    @Override
    public int getDisplayWidth(AttachmentQueryDisplay display) {
        return 170;
    }
}
