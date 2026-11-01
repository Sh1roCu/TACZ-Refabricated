package cn.sh1rocu.tacz.mixin.compat.tweakeroo;

import com.tacz.guns.item.AmmoItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @SuppressWarnings("MixinAnnotationTarget")
    @Inject(method = "getMaxStackSize(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void getMaxStackSize(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (((Item) (Object) this) instanceof AmmoItem item) {
            cir.setReturnValue(item.tacz$getMaxStackSize(stack));
        }
    }
}
