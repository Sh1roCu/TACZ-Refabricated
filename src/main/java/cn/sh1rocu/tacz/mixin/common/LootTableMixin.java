package cn.sh1rocu.tacz.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.tacz.guns.loot.LootTableInjectorModifier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootTable.class)
public class LootTableMixin {
    @ModifyReturnValue(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At(value = "RETURN"))
    private ObjectArrayList<ItemStack> tacz$globalModifier(ObjectArrayList<ItemStack> list, LootContext lootContext) {
        return LootTableInjectorModifier.doApply(list, lootContext, (LootTable) (Object) this);
    }
}