package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IMinecart;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin {
    @Definition(id = "getMinecartType", method = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getMinecartType()Lnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;")
    @Definition(id = "RIDEABLE", field = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;RIDEABLE:Lnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;")
    @Expression("this.getMinecartType() == RIDEABLE")
    @ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
    private boolean tacz$canBeRidden(boolean original) {
        if (this instanceof IMinecart minecart)
            return minecart.tacz$canBeRidden();
        return original;
    }
}
