package com.tacz.guns.compat.jrei.rei.display;

import com.tacz.guns.compat.jrei.common.entry.AttachmentQueryEntry;
import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class AttachmentQueryDisplay implements Display {
    private final AttachmentQueryEntry entry;

    public AttachmentQueryDisplay(AttachmentQueryEntry entry) {
        this.entry = entry;
    }

    public AttachmentQueryEntry getEntry() {
        return entry;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputs = new ArrayList<>();
        entry.getAllowGunStacks().forEach(gun -> inputs.add(EntryIngredients.of(gun)));
        if (!entry.getExtraAllowGunStacks().isEmpty())
            inputs.add(EntryIngredients.ofItemStacks(entry.getExtraAllowGunStacks()));
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(entry.getAttachmentStack()));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GunModREIClientPlugin.ATTACHMENT_QUERY;
    }
}