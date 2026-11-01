package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.mixin.RenderTargetStencil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.nio.IntBuffer;

@Mixin(value = RenderTarget.class, priority = 2000)
public abstract class RenderTargetMixin implements RenderTargetStencil {
    @Shadow
    public abstract void resize(int width, int height, boolean getError);

    @Shadow
    public int viewWidth;
    @Shadow
    public int viewHeight;
    @Unique
    private boolean stencilEnabled = false;

    @WrapOperation(method = "createBuffers", at = @At(remap = false, value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V", ordinal = 0))
    private void initFbo_texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, IntBuffer pixels, Operation<Void> original) {
        if (!this.stencilEnabled) {
            original.call(target, level, internalFormat, width, height, border, format, type, pixels);
        } else {
            GlStateManager._texImage2D(target, level, 36013, width, height, border, 34041, 36269, pixels);
        }
    }

    @WrapOperation(method = "createBuffers", at = @At(remap = false, value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V", ordinal = 1))
    private void initFbo_glFramebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level, Operation<Void> original) {
        if (!stencilEnabled) {
            original.call(target, attachment, textureTarget, texture, level);
        } else {
            GlStateManager._glFramebufferTexture2D(target, attachment, textureTarget, texture, level);
            GlStateManager._glFramebufferTexture2D(target, 36128, textureTarget, texture, level);
        }
    }

    @Override
    public void tacz$enableStencil() {
        if (!stencilEnabled) {
            stencilEnabled = true;
            resize(this.viewWidth, this.viewHeight, Minecraft.ON_OSX);
        }
    }

    @Override
    public boolean tacz$isStencilEnabled() {
        return stencilEnabled;
    }
}
