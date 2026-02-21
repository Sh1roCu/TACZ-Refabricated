package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.event.LivingHurtEvent;
import cn.sh1rocu.tacz.api.event.LivingKnockBackEvent;
import cn.sh1rocu.tacz.api.extension.IItem;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.tacz.guns.init.ModAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    public abstract ItemStack getItemInHand(InteractionHand hand);

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder tacz$createLivingAttributes(AttributeSupplier.Builder original) {
        original.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(ModAttributes.BULLET_RESISTANCE));
        return original;
    }

    @Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
    private void tacz$swingHand(InteractionHand hand, boolean bl, CallbackInfo ci) {
        ItemStack stack = this.getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof IItem swing) {
            if (swing.tacz$onEntitySwing(stack, (LivingEntity) (Object) this))
                ci.cancel();
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At(value = "LOAD", ordinal = 0), index = 3)
    private float tacz$livingHurtEvent(float value, ServerLevel serverLevel, DamageSource pDamageSource, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
        LivingHurtEvent event = new LivingHurtEvent((LivingEntity) (Object) this, pDamageSource, value);
        eventRef.set(event);
        LivingHurtEvent.CALLBACK.invoker().onLivingHurt(event);
        if (event.isCanceled())
            return 0;
        return event.getAmount();
    }

    @Inject(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), cancellable = true)
    private void tacz$shouldCancelHurt(ServerLevel serverLevel, DamageSource damageSource, float f, CallbackInfo ci, @Share("hurt") LocalRef<LivingHurtEvent> eventRef) {
        if (eventRef.get().getAmount() <= 0)
            ci.cancel();
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private double tacz$modifyKnockbackStrength(double strength, double ogstrength, double xRatio, double zRatio, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        LivingKnockBackEvent event = new LivingKnockBackEvent((LivingEntity) (Object) this, (float) strength, xRatio, zRatio);
        LivingKnockBackEvent.CALLBACK.invoker().onLivingKnockBack(event);
        eventRef.set(event);
        if (!event.isCanceled() && event.getOriginalStrength() != event.getStrength()) {
            return event.getStrength();
        }
        return strength;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private double tacz$modifyRatioX(double ratioX, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        var event = eventRef.get();
        if (event.getOriginalRatioX() != event.getRatioX())
            return event.getRatioX();
        return ratioX;
    }

    @ModifyVariable(method = "knockback", at = @At("HEAD"), ordinal = 2, argsOnly = true)
    private double tacz$modifyRatioZ(double ratioZ, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        var event = eventRef.get();
        if (event.getOriginalRatioZ() != event.getRatioZ())
            return event.getRatioZ();
        return ratioZ;
    }

    @Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributeValue(Lnet/minecraft/core/Holder;)D"), cancellable = true)
    private void tacz$shouldCancelKnockback(double strength, double xRatio, double zRatio, CallbackInfo ci, @Share("event") LocalRef<LivingKnockBackEvent> eventRef) {
        if (eventRef.get().isCanceled())
            ci.cancel();
    }
}
