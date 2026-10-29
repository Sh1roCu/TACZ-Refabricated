package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.event.RenderPlayerEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), cancellable = true)
    private void goety$onRenderPlayerPre(AbstractClientPlayer player, float f, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        var event = new RenderPlayerEvent.Pre(player, (PlayerRenderer) (Object) this, partialTick, poseStack, buffer, packedLight);
        RenderPlayerEvent.PRE.invoker().post(event);
        if (event.isCanceled())
            ci.cancel();
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
    private void tacz$onRenderPlayerPost(AbstractClientPlayer player, float f, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        var event = new RenderPlayerEvent.Post(player, (PlayerRenderer) (Object) this, partialTick, poseStack, buffer, packedLight);
        RenderPlayerEvent.POST.invoker().post(event);
    }
}