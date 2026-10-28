package cn.sh1rocu.tacz.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import com.tacz.guns.compat.immediatelyfast.ImmediatelyFastCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "renderSlot", at = @At("HEAD"))
    private void tacz$renderHotbarItemPre(GuiGraphics context, int x, int y, float f, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        ImmediatelyFastCompat.renderHotbarItem(stack, true);
    }

    @Inject(method = "renderSlot", at = @At("RETURN"))
    private void tacz$renderHotbarItemPost(GuiGraphics context, int x, int y, float f, Player player, ItemStack stack, int seed, CallbackInfo ci) {
        ImmediatelyFastCompat.renderHotbarItem(stack, false);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void tacz$onRender(GuiGraphics context, float tickDelta, CallbackInfo ci) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        PreventsHotbarEvent.onRenderHotbarEvent(cancelled);
        if (cancelled.get()) {
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean tacz$renderCrosshairPre(Gui instance, GuiGraphics guiGraphics, @Local(argsOnly = true) float partialTick) {
        var cancelled = new AtomicBoolean(false);
        RenderCrosshairEvent.onRenderOverlay(guiGraphics, Minecraft.getInstance().getWindow(), partialTick, cancelled);
        return !cancelled.get();
    }
}
