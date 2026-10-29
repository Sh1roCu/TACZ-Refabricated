package com.tacz.guns.client.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.tacz.guns.block.GunPegboardBlock;
import com.tacz.guns.block.GunPegboardSlotHelper;
import com.tacz.guns.block.entity.GunPegboardBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public class GunPegboardHighlightEvent {
    public static boolean onRenderBlockHighlight(WorldRenderContext worldRenderContext, BlockHitResult target) {
        BlockPos pos = target.getBlockPos();
        Entity cameraEntity = worldRenderContext.camera().getEntity();
        Level level = cameraEntity.level();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof GunPegboardBlock)) {
            return true;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        GunPegboardBlockEntity pegboard = blockEntity instanceof GunPegboardBlockEntity value ? value : null;
        if (cameraEntity.isShiftKeyDown()) {
            renderOccupiedSlots(worldRenderContext, state, pos, pegboard);
            return true;
        }
        renderAimedSlot(worldRenderContext, state, pos, target, pegboard);

        return true;
    }

    private static void renderAimedSlot(WorldRenderContext context, BlockState state, BlockPos pos, BlockHitResult target, GunPegboardBlockEntity pegboard) {
        int slot = GunPegboardSlotHelper.getSlot(state, pos, target.getLocation());
        if (slot < 0) {
            return;
        }
        boolean occupied = pegboard != null && !pegboard.getItem(slot).isEmpty();
        renderSlotBox(context, state, pos, slot, occupied ? 1.0F : 1.0F, occupied ? 0.0F : 1.0F, 0.0F);
    }

    private static void renderOccupiedSlots(WorldRenderContext context, BlockState state, BlockPos pos, GunPegboardBlockEntity pegboard) {
        if (pegboard == null) {
            return;
        }
        for (int slot = 0; slot < GunPegboardBlockEntity.SLOT_COUNT; slot++) {
            if (!pegboard.getItem(slot).isEmpty()) {
                renderSlotBox(context, state, pos, slot, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    private static void renderSlotBox(WorldRenderContext context, BlockState state, BlockPos pos, int slot, float red, float green, float blue) {
        var consumer = context.consumers();
        if (consumer == null)
            return;

        Vec3 camera = context.camera().getPosition();
        AABB aabb = GunPegboardSlotHelper.getSlotAabb(state, pos, slot).move(-camera.x, -camera.y, -camera.z);
        VertexConsumer buffer = consumer.getBuffer(PegboardHighlightRenderType.getLines());
        LevelRenderer.renderLineBox(context.matrixStack(), buffer, aabb, red, green, blue, 1.0F);
    }

    private static class PegboardHighlightRenderType extends RenderStateShard {
        private static final RenderType LINES_NO_DEPTH = RenderType.create(
                "tacz_pegboard_highlight",
                DefaultVertexFormat.POSITION_COLOR_NORMAL,
                VertexFormat.Mode.LINES,
                256,
                false,
                false,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_LINES_SHADER)
                        .setLineState(DEFAULT_LINE)
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .createCompositeState(false)
        );

        private PegboardHighlightRenderType(String name, Runnable setupState, Runnable clearState) {
            super(name, setupState, clearState);
        }

        private static RenderType getLines() {
            return LINES_NO_DEPTH;
        }
    }
}
