package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.event.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"), cancellable = true)
    private void tacz$onMouseButtonPre(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        InputEvent.MouseButton.Pre event = new InputEvent.MouseButton.Pre(button, action, modifiers);
        InputEvent.MouseButton.Pre.EVENT.invoker().onMousePre(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "onPress", at = @At("TAIL"))
    private void tacz$onMouseButtonPost(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if (windowPointer == this.minecraft.getWindow().getWindow()) {
            InputEvent.MouseButton.Post event = new InputEvent.MouseButton.Post(button, action, modifiers);
            InputEvent.MouseButton.Post.EVENT.invoker().onMousePost(event);
        }
    }
}