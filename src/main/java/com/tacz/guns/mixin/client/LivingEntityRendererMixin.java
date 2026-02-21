package com.tacz.guns.mixin.client;

import com.tacz.guns.util.RenderStateEntityData;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stores the LivingEntity reference in the render state using Fabric's RenderStateDataKey,
 * so it can be retrieved during setupAnim where only the render state is available.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void tacz$storeEntityInRenderState(LivingEntity entity, LivingEntityRenderState state, float partialTick, CallbackInfo ci) {
        RenderStateEntityData.storeEntity(state, entity);
    }
}
