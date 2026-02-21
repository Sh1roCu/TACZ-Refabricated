package cn.sh1rocu.tacz.api.extension;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface IItem {
    default boolean tacz$onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Environment(EnvType.CLIENT)
    Object getCustomRenderer();
}
