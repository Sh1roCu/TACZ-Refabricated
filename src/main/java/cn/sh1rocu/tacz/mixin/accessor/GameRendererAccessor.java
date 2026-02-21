package cn.sh1rocu.tacz.mixin.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Invoker("getFov")
    float tacz$getFov(Camera camera, float tickDelta, boolean useFovSetting);

    @Invoker("bobHurt")
    void tacz$bobHurt(PoseStack poseStack, float tickDelta);

    @Invoker("bobView")
    void tacz$bobView(PoseStack poseStack, float tickDelta);
}
