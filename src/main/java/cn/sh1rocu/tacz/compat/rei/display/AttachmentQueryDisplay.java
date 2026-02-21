package cn.sh1rocu.tacz.compat.rei.display;

import cn.sh1rocu.tacz.compat.rei.REIClientPlugin;
import cn.sh1rocu.tacz.compat.rei.entry.AttachmentQueryEntry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
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
        return REIClientPlugin.ATTACHMENT_QUERY;
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        throw new UnsupportedOperationException("AttachmentQueryDisplay does not support serialization");
    }

    @Override
    public java.util.Optional<net.minecraft.resources.Identifier> getDisplayLocation() {
        return java.util.Optional.empty();
    }
}