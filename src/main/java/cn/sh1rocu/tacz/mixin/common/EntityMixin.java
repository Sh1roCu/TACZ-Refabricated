package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.event.EntityRemoveEvent;
import cn.sh1rocu.tacz.api.extension.IEntityPersistentData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin implements IEntityPersistentData {
    @Shadow
    private Level level;

    @Inject(method = "remove", at = @At("TAIL"))
    private void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!this.level.isClientSide) {
            EntityRemoveEvent event = new EntityRemoveEvent((Entity) (Object) this);
            EntityRemoveEvent.EVENT.invoker().onEntityRemove(event);
        }
    }

    @Unique
    private CompoundTag tacz$persistentData;

    @Unique
    @Override
    public CompoundTag tacz$getPersistentData() {
        if (this.tacz$persistentData == null) {
            this.tacz$persistentData = new CompoundTag();
        }
        return tacz$persistentData;
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void tacz$savePersistentData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
        if (this.tacz$persistentData != null) {
            nbt.put("ForgeData", this.tacz$persistentData.copy());
        }
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void tacz$loadPersistentData(CompoundTag nbt, CallbackInfo ci) {
        if (nbt.contains("ForgeData", 10)) {
            tacz$persistentData = nbt.getCompound("ForgeData");
        }
    }
}