package com.tacz.guns.mixin.client;

import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractButton.class)
public class AbstractButtonMixin {
    /**
     * Record button click time, to provide shooting cooldown and prevent accidental firing after clicking a button
     */
    @Inject(method = "onClick(Lnet/minecraft/client/input/MouseButtonEvent;Z)V", at = @At("HEAD"))
    public void onClickHead(MouseButtonEvent mouseButtonEvent, boolean focused, CallbackInfo ci) {
        LocalPlayerDataHolder.clientClickButtonTimestamp = System.currentTimeMillis();
    }
}
