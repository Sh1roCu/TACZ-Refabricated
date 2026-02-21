package com.tacz.guns.client.model.functional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.client.resource.pojo.display.LaserConfig;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.util.LaserColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public class BeamRenderer {
    public static final Identifier LASER_BEAM_TEXTURE = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "textures/entity/beam.png");
    private static final LaserConfig DEFAULT_LASER_CONFIG = new LaserConfig();

    public static void renderLaserBeam(ItemStack stack, PoseStack poseStack, ItemDisplayContext transformType, @Nonnull List<BedrockPart> path) {
        if (stack == null || !transformType.firstPerson() && !(transformType == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND)) {
            return;
        }

        // TODO: ARCompat.shouldAccelerate() disabled - AR compat excluded from compilation

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = bufferSource.getBuffer(LaserBeamRenderState.getLaserBeam());
        poseStack.pushPose();
        {
            for (int i = 0; i < path.size(); ++i) {
                path.get(i).translateAndRotateAndScale(poseStack);
            }

            LaserConfig laserConfig = getLaserConfig(stack);

            int color = LaserColorUtil.getLaserColor(stack, laserConfig);
            int r = (color >> 16) & 0xFF;
            int g = (color >> 8) & 0xFF;
            int b = color & 0xFF;

            stringVertex(transformType.firstPerson() ? -laserConfig.getLength() : -laserConfig.getLengthThird(),
                    transformType.firstPerson() ? laserConfig.getWidth() : laserConfig.getWidthThird(),
                    builder, poseStack.last(), r, g, b, RenderConfig.ENABLE_LASER_FADE_OUT.get());
        }
        poseStack.popPose();
    }

    // TODO: renderLaserBeamAccelerated disabled - AR compat excluded from compilation
    public static boolean renderLaserBeamAccelerated(ItemStack stack, PoseStack poseStack, ItemDisplayContext transformType, @Nonnull List<BedrockPart> path) {
        return false;
    }

    private static LaserConfig getLaserConfig(ItemStack stack) {
        if (stack == null) {
            return DEFAULT_LASER_CONFIG;
        }

        if (stack.getItem() instanceof IAttachment iAttachment) {
            return TimelessAPI.getClientAttachmentIndex(iAttachment.getAttachmentId(stack))
                    .map(ClientAttachmentIndex::getLaserConfig)
                    .orElse(DEFAULT_LASER_CONFIG);
        }

        if (stack.getItem() instanceof IGun) {
            return TimelessAPI.getGunDisplay(stack)
                    .map(GunDisplayInstance::getLaserConfig)
                    .orElse(DEFAULT_LASER_CONFIG);
        }

        return DEFAULT_LASER_CONFIG;
    }

    private static void stringVertex(float z, float width, VertexConsumer pConsumer, PoseStack.Pose pPose, int r, int g, int b, boolean fadeOut) {
        float halfWidth = width / 2;
        int endAlpha = fadeOut ? 0 : 255;
        int light = LightTexture.pack(15, 15);
        int overlay = net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;
        pConsumer.addVertex(pPose.pose(), -halfWidth, -halfWidth, 0).setColor(r, g, b, 255).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(pPose, -1, 0, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, halfWidth, 0).setColor(r, g, b, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pPose, -1, 0, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pPose, -1, 0, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, -halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pPose, -1, 0, 0);

        pConsumer.addVertex(pPose.pose(), -halfWidth, halfWidth, 0).setColor(r, g, b, 255).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(pPose, 0, 1, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, halfWidth, 0).setColor(r, g, b, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 0, 1, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 0, 1, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pPose, 0, 1, 0);

        pConsumer.addVertex(pPose.pose(), halfWidth, halfWidth, 0).setColor(r, g, b, 255).setUv(0, 0).setOverlay(overlay).setLight(light).setNormal(pPose, 1, 0, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, -halfWidth, 0).setColor(r, g, b, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 1, 0, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, -halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 1, 0, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pPose, 1, 0, 0);

        pConsumer.addVertex(pPose.pose(), halfWidth, -halfWidth, 0).setColor(r, g, b, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 0, -1, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, -halfWidth, 0).setColor(r, g, b, 255).setUv(0, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 0, -1, 0);
        pConsumer.addVertex(pPose.pose(), -halfWidth, -halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 1).setOverlay(overlay).setLight(light).setNormal(pPose, 0, -1, 0);
        pConsumer.addVertex(pPose.pose(), halfWidth, -halfWidth, z).setColor(r, g, b, endAlpha).setUv(1, 0).setOverlay(overlay).setLight(light).setNormal(pPose, 0, -1, 0);
    }

    /**
     * In 1.21.11, RenderStateShard and RenderType.CompositeState were removed.
     * Custom render types now use RenderPipeline/RenderSetup.
     * As a simplification, we use entityTranslucentEmissive which provides similar
     * visual behavior (emissive, translucent, with lightmap).
     * TODO: Implement custom additive blending via RenderPipeline if needed.
     */
    public static class LaserBeamRenderState {
        // Use entityTranslucentEmissive as a close approximation for laser beam rendering
        protected static final RenderType LASER_BEAM = net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(LASER_BEAM_TEXTURE);
        protected static final RenderType LASER_BEAM_ENTITY = net.minecraft.client.renderer.rendertype.RenderTypes.entityTranslucentEmissive(LASER_BEAM_TEXTURE);

        public static RenderType getLaserBeam() {
            return LASER_BEAM;
        }

        public static RenderType getLaserBeamEntity() {
            return LASER_BEAM_ENTITY;
        }
    }
}