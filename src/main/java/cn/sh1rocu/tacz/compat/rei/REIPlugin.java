package cn.sh1rocu.tacz.compat.rei;

import com.tacz.guns.api.item.gun.GunItemManager;
import com.tacz.guns.init.ModItems;
import me.shedaniel.rei.api.common.entry.comparison.ItemComparatorRegistry;

public class REIPlugin implements me.shedaniel.rei.api.common.plugins.REICommonPlugin {
    @Override
    public void registerItemComparators(ItemComparatorRegistry registry) {
        registry.register(REISubtype.getAmmoSubtype(), ModItems.AMMO);
        registry.register(REISubtype.getAttachmentSubtype(), ModItems.ATTACHMENT);
        registry.register(REISubtype.getAmmoBoxSubtype(), ModItems.AMMO_BOX);
        GunItemManager.getAllGunItems().forEach(item ->
                registry.register(REISubtype.getGunSubtype(), item));
    }
}
