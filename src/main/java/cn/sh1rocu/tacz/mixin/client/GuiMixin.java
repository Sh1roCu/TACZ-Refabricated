package cn.sh1rocu.tacz.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.event.PreventsHotbarEvent;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import com.tacz.guns.compat.immediatelyfast.ImmediatelyFastCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
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

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", ordinal = 1, shift = At.Shift.BEFORE, remap = false))
    private void tacz$renderCrosshairPre(GuiGraphics context, float tickDelta, CallbackInfo ci) {
        RenderCrosshairEvent.onRenderOverlay(context, Minecraft.getInstance().getWindow());
    }

    // 需要渲染枪械准心时取消原版渲染
    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void tacz$renderCrosshair(GuiGraphics context, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return;
        }

        RenderSystem.defaultBlendFunc();

        ci.cancel();
    }
}
