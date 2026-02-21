package com.tacz.guns.util;

import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;

/**
 * Utility for storing entity references in render states using Fabric's RenderStateDataKey.
 * In 1.21.11, setupAnim only receives render state objects, not the entity itself.
 * This allows retrieving the source entity in model setupAnim methods.
 */
public class RenderStateEntityData {
    public static final RenderStateDataKey<LivingEntity> LIVING_ENTITY_KEY =
            RenderStateDataKey.create(() -> "tacz:living_entity");

    public static void storeEntity(EntityRenderState state, LivingEntity entity) {
        ((FabricRenderState) state).setData(LIVING_ENTITY_KEY, entity);
    }

    public static LivingEntity getEntity(EntityRenderState state) {
        return ((FabricRenderState) state).getData(LIVING_ENTITY_KEY);
    }
}
