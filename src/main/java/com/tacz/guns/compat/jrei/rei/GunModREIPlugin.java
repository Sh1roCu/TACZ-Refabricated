package com.tacz.guns.compat.jrei.rei;

import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.init.ModItems;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;
import me.shedaniel.rei.api.common.plugins.REIPlugin;

public class GunModREIPlugin implements REIPlugin<GunModREIPlugin> {
    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.register(GunModREISubtype.getAmmoSubtype(), ModItems.AMMO);
        registry.register(GunModREISubtype.getAttachmentSubtype(), ModItems.ATTACHMENT);
        registry.register(GunModREISubtype.getAmmoBoxSubtype(), ModItems.AMMO_BOX);
        registry.register(GunModREISubtype.getTableSubType(), ModItems.WORKBENCH_111);
        registry.register(GunModREISubtype.getTableSubType(), ModItems.WORKBENCH_121);
        registry.register(GunModREISubtype.getTableSubType(), ModItems.WORKBENCH_211);
        GunItemManager.getAllGunItems().forEach(item ->
                registry.register(GunModREISubtype.getGunSubtype(), item));
    }


    @Override
    public Class<GunModREIPlugin> getPluginProviderClass() {
        return GunModREIPlugin.class;
    }
}
