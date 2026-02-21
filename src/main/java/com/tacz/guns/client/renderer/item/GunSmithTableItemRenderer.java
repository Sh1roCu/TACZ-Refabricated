package com.tacz.guns.client.renderer.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.tacz.guns.client.model.SlotModel;
import com.tacz.guns.client.model.bedrock.BedrockModel;
import com.tacz.guns.client.renderer.block.GunSmithTableRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class GunSmithTableItemRenderer {
    private static final SlotModel SLOT_BLOCK_MODEL = new SlotModel();

    public static final Supplier<GunSmithTableItemRenderer> INSTANCE = Suppliers.memoize(GunSmithTableItemRenderer::new);

    public GunSmithTableItemRenderer() {
    }

    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext transformType, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        GunSmithTableRenderer.getIndex(stack).ifPresentOrElse(index -> {
            BedrockModel model = index.getModel();
            Identifier texture = index.getTexture();
            if (model == null) {
                return;
            }
            poseStack.pushPose();

            ItemTransforms transforms = index.getTransforms();
            if (transforms != null) {
                poseStack.translate(0.5F, 0.5F, 0.5F);
                ItemTransform transform = transforms.getTransform(transformType);
                transform.apply(false, poseStack.last());
                poseStack.translate(-0.5F, -0.5F, -0.5F);
            }

            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            RenderType renderType = RenderTypes.entityTranslucent(texture);
            model.render(poseStack, transformType, renderType, pPackedLight, pPackedOverlay);
            poseStack.popPose();
        }, () -> {
            poseStack.translate(0.5, 1.5, 0.5);
            poseStack.mulPose(Axis.ZN.rotationDegrees(180));
            VertexConsumer buffer = pBuffer.getBuffer(RenderTypes.entityTranslucent(MissingTextureAtlasSprite.getLocation()));
            SLOT_BLOCK_MODEL.renderToBuffer(poseStack, buffer, pPackedLight, pPackedOverlay);
        });
    }
}
