package com.tacz.guns.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.client.model.BedrockAttachmentModel;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.resource.index.ClientAttachmentIndex;
import com.tacz.guns.util.RenderDistance;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AttachmentItemRenderer {
    public static final SlotModel SLOT_ATTACHMENT_MODEL = new SlotModel();

    public static final Supplier<AttachmentItemRenderer> INSTANCE = Suppliers.memoize(AttachmentItemRenderer::new);

    public AttachmentItemRenderer() {
    }

    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transformType, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        if (stack.getItem() instanceof IAttachment iAttachment) {
            Identifier attachmentId = iAttachment.getAttachmentId(stack);
            poseStack.pushPose();
            TimelessAPI.getClientAttachmentIndex(attachmentId).ifPresentOrElse(attachmentIndex -> {
                // GUI 特殊渲染
                if (transformType == ItemDisplayContext.GUI) {
                    poseStack.translate(0.5, 1.5, 0.5);
                    poseStack.mulPose(Axis.ZN.rotationDegrees(180));
                    VertexConsumer buffer = pBuffer.getBuffer(RenderTypes.entityTranslucent(attachmentIndex.getSlotTexture()));
                    SLOT_ATTACHMENT_MODEL.renderToBuffer(poseStack, buffer, pPackedLight, pPackedOverlay);
                    return;
                }
                poseStack.translate(0.5, 2, 0.5);
                // 反转模型
                poseStack.scale(-1, -1, 1);
                if (transformType == ItemDisplayContext.FIXED) {
                    poseStack.mulPose(Axis.YN.rotationDegrees(90f));
                }
                this.renderDefaultAttachment(transformType, poseStack, pBuffer, pPackedLight, pPackedOverlay, attachmentIndex);
            }, () -> {
                // 没有这个 attachmentId，渲染黑紫材质以提醒
                poseStack.translate(0.5, 1.5, 0.5);
                poseStack.mulPose(Axis.ZN.rotationDegrees(180));
                VertexConsumer buffer = pBuffer.getBuffer(RenderTypes.entityTranslucent(MissingTextureAtlasSprite.getLocation()));
                SLOT_ATTACHMENT_MODEL.renderToBuffer(poseStack, buffer, pPackedLight, pPackedOverlay);
            });
            poseStack.popPose();
        }
    }

    private void renderDefaultAttachment(@NotNull ItemDisplayContext transformType, @NotNull PoseStack poseStack, @NotNull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, ClientAttachmentIndex attachmentIndex) {
        BedrockAttachmentModel model = attachmentIndex.getAttachmentModel();
        Identifier texture = attachmentIndex.getModelTexture();
        // 有模型？正常渲染
        if (model != null && texture != null) {
            // 调用低模
            Pair<BedrockAttachmentModel, Identifier> lodModel = attachmentIndex.getLodModel();
            // 有低模、在高模渲染范围外、不是第一人称
            if (lodModel != null && !RenderDistance.inRenderHighPolyModelDistance(poseStack) && !transformType.firstPerson()) {
                model = lodModel.getLeft();
                texture = lodModel.getRight();
            }
            RenderType renderType = RenderTypes.entityCutout(texture);
            model.render(null, null, poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
        }
        // 否则，以 GUI 形式渲染
        else {
            poseStack.translate(0, 0.5, 0);
            // 展示框里显示正常
            if (transformType == ItemDisplayContext.FIXED) {
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
            }
            VertexConsumer buffer = pBuffer.getBuffer(RenderTypes.entityTranslucent(attachmentIndex.getSlotTexture()));
            SLOT_ATTACHMENT_MODEL.renderToBuffer(poseStack, buffer, pPackedLight, pPackedOverlay);
        }
    }
}
