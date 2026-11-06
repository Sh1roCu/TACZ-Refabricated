package com.tacz.guns.compat.jrei.rei.display;

import com.tacz.guns.compat.jrei.common.entry.GunAmmoQueryEntry;
import com.tacz.guns.compat.jrei.rei.GunModREIClientPlugin;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.ArrayList;
import java.util.List;

public class GunAmmoQueryDisplay implements Display {
    private final GunAmmoQueryEntry entry;

    public GunAmmoQueryDisplay(GunAmmoQueryEntry entry) {
        this.entry = entry;
    }

    public GunAmmoQueryEntry getEntry() {
        return entry;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        List<EntryIngredient> inputs = new ArrayList<>();
        inputs.add(EntryIngredients.of(entry.getAmmoStack()));
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return List.of(EntryIngredients.of(entry.getGunStack()));
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return GunModREIClientPlugin.GUN_AMMO_QUERY;
    }
}
