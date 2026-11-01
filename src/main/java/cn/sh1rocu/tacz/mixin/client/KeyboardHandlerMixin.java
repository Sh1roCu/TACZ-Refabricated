package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.event.InputEvent;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "keyPress", at = @At("TAIL"))
    private void tacz$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (window == this.minecraft.getWindow().getWindow()) {
            InputEvent.Key.EVENT.invoker().onKey(new InputEvent.Key(key, scancode, action, modifiers));
        }
    }
}
