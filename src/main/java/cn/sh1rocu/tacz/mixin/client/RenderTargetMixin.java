package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.mixin.RenderTargetStencil;
import com.mojang.blaze3d.pipeline.RenderTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

// TODO: In 1.21.11 the rendering pipeline was overhauled from direct OpenGL to a GpuDevice abstraction.
// The createBuffers method no longer calls GlStateManager._texImage2D or _glFramebufferTexture2D,
// so the stencil buffer @WrapOperation injections have been removed.
// Stencil buffer support needs to be reimplemented using the new GpuDevice/GpuTexture API.
@Mixin(value = RenderTarget.class, priority = 2000)
public abstract class RenderTargetMixin implements RenderTargetStencil {
    @Shadow
    public abstract void resize(int width, int height);

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Unique
    private boolean stencilEnabled = false;

    @Override
    public void tacz$enableStencil() {
        if (!stencilEnabled) {
            stencilEnabled = true;
            // TODO: Stencil buffer creation needs reimplementation for the new GPU abstraction layer
            resize(this.width, this.height);
        }
    }

    @Override
    public boolean tacz$isStencilEnabled() {
        return stencilEnabled;
    }
}
