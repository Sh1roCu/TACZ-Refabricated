package com.tacz.guns.util;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.tacz.guns.compat.optifine.OptifineCompat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

@Environment(EnvType.CLIENT)
public final class RenderHelper {
    public static void blit(PoseStack poseStack, float x, float y, float uOffset, float vOffset, float pWidth, float height, float textureWidth, float textureHeight) {
        blit(poseStack, x, y, pWidth, height, uOffset, vOffset, pWidth, height, textureWidth, textureHeight);
    }

    private static void blit(PoseStack poseStack, float x, float y, float pWidth, float height, float uOffset, float vOffset, float uWidth, float vHeight, float textureWidth, float textureHeight) {
        innerBlit(poseStack, x, x + pWidth, y, y + height, 0, uWidth, vHeight, uOffset, vOffset, textureWidth, textureHeight);
    }

    private static void innerBlit(PoseStack poseStack, float x1, float x2, float y1, float y2, float blitOffset, float uWidth, float vHeight, float uOffset, float vOffset, float textureWidth, float textureHeight) {
        innerBlit(poseStack.last().pose(), x1, x2, y1, y2, blitOffset, (uOffset + 0.0F) / textureWidth, (uOffset + uWidth) / textureWidth, (vOffset + 0.0F) / textureHeight, (vOffset + vHeight) / textureHeight);
    }

    private static void innerBlit(Matrix4f matrix, float x1, float x2, float y1, float y2, float blitOffset, float minU, float maxU, float minV, float maxV) {
        // In 1.21.11 BufferUploader was removed; use direct GL for simple textured quad rendering
        GL11.glBegin(GL11.GL_QUADS);
        float[] v0 = transformVertex(matrix, x1, y2, blitOffset);
        GL11.glTexCoord2f(minU, maxV); GL11.glVertex3f(v0[0], v0[1], v0[2]);
        float[] v1 = transformVertex(matrix, x2, y2, blitOffset);
        GL11.glTexCoord2f(maxU, maxV); GL11.glVertex3f(v1[0], v1[1], v1[2]);
        float[] v2 = transformVertex(matrix, x2, y1, blitOffset);
        GL11.glTexCoord2f(maxU, minV); GL11.glVertex3f(v2[0], v2[1], v2[2]);
        float[] v3 = transformVertex(matrix, x1, y1, blitOffset);
        GL11.glTexCoord2f(minU, minV); GL11.glVertex3f(v3[0], v3[1], v3[2]);
        GL11.glEnd();
    }

    private static float[] transformVertex(Matrix4f matrix, float x, float y, float z) {
        org.joml.Vector4f v = new org.joml.Vector4f(x, y, z, 1.0f);
        v.mul(matrix);
        return new float[]{v.x(), v.y(), v.z()};
    }

    public static void enableItemEntityStencilTest() {
        RenderSystem.assertOnRenderThread();
        if (OptifineCompat.isOptifineInstalled()) {
            // 以下代码用于应对 使用 optifine 的场景
            int depthTextureId = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME);
            int stencilTextureId = GL30.glGetFramebufferAttachmentParameteri(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE);
            if (depthTextureId != GL30.GL_NONE && stencilTextureId == GL30.GL_NONE) {
                GL30.glBindTexture(GL30.GL_TEXTURE_2D, depthTextureId);
                int dataType = GL30.glGetTexLevelParameteri(GL30.GL_TEXTURE_2D, 0, GL30.GL_TEXTURE_DEPTH_TYPE);
                if (dataType == GL30.GL_UNSIGNED_NORMALIZED) {
                    int width = GL30.glGetTexLevelParameteri(GL30.GL_TEXTURE_2D, 0, GL30.GL_TEXTURE_WIDTH);
                    int height = GL30.glGetTexLevelParameteri(GL30.GL_TEXTURE_2D, 0, GL30.GL_TEXTURE_HEIGHT);
                    GlStateManager._texImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH24_STENCIL8, width, height, 0, GL30.GL_DEPTH_STENCIL, GL30.GL_UNSIGNED_INT_24_8, null);
                    GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, 3553, depthTextureId, 0);
                }
            }
        } else {
            Minecraft.getInstance().getMainRenderTarget().tacz$enableStencil();
        }
        GL11.glEnable(GL11.GL_STENCIL_TEST);
    }

    public static void disableItemEntityStencilTest() {
        RenderSystem.assertOnRenderThread();
        GL11.glDisable(GL11.GL_STENCIL_TEST);
    }

    // TODO: In 1.21.11, AvatarRenderer.renderRightHand/renderLeftHand now requires SubmitNodeCollector
    // instead of MultiBufferSource. This method needs rearchitecting for the new rendering pipeline.
    public static void renderFirstPersonArm(LocalPlayer player, HumanoidArm hand, PoseStack matrixStack, int combinedLight) {
        // No-op: hand rendering requires SubmitNodeCollector in 1.21.11 pipeline
    }
}
