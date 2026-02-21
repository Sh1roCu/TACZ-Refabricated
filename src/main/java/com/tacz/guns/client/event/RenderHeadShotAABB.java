package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.RenderLivingEvent;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.config.util.HeadShotAABBConfigRead;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

@Environment(EnvType.CLIENT)
public class RenderHeadShotAABB {
    public static void onRenderEntity(RenderLivingEvent.Post<?, ?, ?> event) {
        // TODO: In 1.21.11, EntityRenderDispatcher.shouldRenderHitBoxes() and LevelRenderer.renderLineBox() were removed.
        // Debug hitbox rendering for headshot AABBs needs to be reimplemented using the new rendering pipeline.
        if (!RenderConfig.HEAD_SHOT_DEBUG_HITBOX.get()) {
            return;
        }
        // Rendering is currently disabled pending migration to new debug rendering system.
    }
}
