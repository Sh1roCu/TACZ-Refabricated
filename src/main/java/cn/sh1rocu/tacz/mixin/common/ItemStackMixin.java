package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.extension.IItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract Item getItem();

    @ModifyReturnValue(method = "getMaxStackSize", at = @At("RETURN"))
    private int tacz$getMaxStackSize(int original) {
        if (this.getItem() instanceof IItem item) {
            ItemStack stack = (ItemStack) (Object) this;
            return item.tacz$getMaxStackSize(stack);
        }
        return original;
    }
}
