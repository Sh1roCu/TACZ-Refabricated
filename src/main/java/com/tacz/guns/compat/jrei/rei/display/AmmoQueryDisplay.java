package com.tacz.guns.compat.jrei.rei.display;

import com.tacz.guns.compat.jrei.common.entry.AmmoQueryEntry;
import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class AmmoQueryDisplay implements Display {
    private final AmmoQueryEntry entry;

    public AmmoQueryDisplay(AmmoQueryEntry entry) {
        this.entry = entry;
    }

    public AmmoQueryEntry getEntry() {
        return entry;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputs = new ArrayList<>();
        entry.getGunStacks().forEach(gun -> inputs.add(EntryIngredients.of(gun)));
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(entry.getAmmoStack()));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GunModREIClientPlugin.AMMO_QUERY;
    }
}
