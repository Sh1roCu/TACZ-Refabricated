package com.tacz.guns.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.functional.MuzzleFlashRender;
import com.tacz.guns.client.model.functional.ShellRender;
import com.tacz.guns.client.renderer.other.HumanoidOffhandRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandLayer.class)
public class ItemInHandLayerMixin {
    @Inject(method = "submit(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;ILnet/minecraft/client/renderer/entity/state/ArmedEntityRenderState;FF)V", at = @At(value = "TAIL"))
    private void submit(PoseStack matrixStack, SubmitNodeCollector collector, int packedLight, ArmedEntityRenderState state, float f, float g, CallbackInfo ci) {
        MuzzleFlashRender.isSelf = false;
        ShellRender.isSelf = false;
        // TODO: HumanoidOffhandRender needs LivingEntity but we only have ArmedEntityRenderState in 1.21.11
        // HumanoidOffhandRender.renderGun(livingEntity, matrixStack, collector, packedLight);
    }

    @Inject(method = "submitArmWithItem", at = @At(value = "HEAD"), cancellable = true)
    private void submitArmWithItemHead(ArmedEntityRenderState state, ItemStackRenderState renderState, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        // TODO: Cannot directly compare entity with render state in 1.21.11
        // if (livingEntity.equals(player)) {
            MuzzleFlashRender.isSelf = true;
            ShellRender.isSelf = true;
        // }
        if (IGun.getIGunOrNull(itemStack) != null && arm == HumanoidArm.LEFT) {
            ci.cancel();
        }
    }

    @Inject(method = "submitArmWithItem", at = @At(value = "TAIL"))
    private void submitArmWithItemTail(ArmedEntityRenderState state, ItemStackRenderState renderState, ItemStack itemStack, HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int packedLight, CallbackInfo ci) {
        MuzzleFlashRender.isSelf = false;
        ShellRender.isSelf = false;
    }
}
