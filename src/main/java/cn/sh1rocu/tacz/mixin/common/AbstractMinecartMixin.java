package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IMinecart;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {
    @ModifyReturnValue(method = "isRideable", at = @At("RETURN"))
    private boolean tacz$canBeRidden(boolean original) {
        if (this instanceof IMinecart minecart)
            return minecart.tacz$canBeRidden();
        return original;
    }
}
