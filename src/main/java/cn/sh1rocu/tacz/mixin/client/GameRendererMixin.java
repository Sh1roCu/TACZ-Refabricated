package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.event.ViewportEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
    private float tacz$viewportFovEvent(float original, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true) float partialTicks, @Local(argsOnly = true) boolean useConfigured) {
        var event = new ViewportEvent.ComputeFov((GameRenderer) (Object) this, camera, partialTicks, original, useConfigured);
        ViewportEvent.FOV.invoker().post(event);
        // TODO: ZoomifyCompat.getFov() disabled - zoomify compat excluded from compilation
        return (float) event.getFOV();
    }
}
