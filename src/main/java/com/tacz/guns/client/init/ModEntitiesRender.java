package com.tacz.guns.client.init;

import com.tacz.guns.block.entity.GunSmithTableBlockEntity;
import com.tacz.guns.block.entity.GunDisplayCaseBlockEntity;
import com.tacz.guns.block.entity.GunPegboardBlockEntity;
import com.tacz.guns.block.entity.StatueBlockEntity;
import com.tacz.guns.block.entity.TargetBlockEntity;
import com.tacz.guns.client.renderer.block.GunSmithTableRenderer;
import com.tacz.guns.client.renderer.block.GunDisplayCaseRenderer;
import com.tacz.guns.client.renderer.block.GunPegboardRenderer;
import com.tacz.guns.client.renderer.block.StatueRenderer;
import com.tacz.guns.client.renderer.block.TargetRenderer;
import com.tacz.guns.client.renderer.entity.EntityBulletRenderer;
import com.tacz.guns.client.renderer.entity.TargetMinecartRenderer;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.entity.TargetMinecart;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

@Environment(EnvType.CLIENT)
public class ModEntitiesRender {
    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityKineticBullet.TYPE, EntityBulletRenderer::new);
        EntityRendererRegistry.register(TargetMinecart.TYPE, TargetMinecartRenderer::new);
        BlockEntityRenderers.register(GunSmithTableBlockEntity.TYPE, GunSmithTableRenderer::new);
        BlockEntityRenderers.register(TargetBlockEntity.TYPE, TargetRenderer::new);
        BlockEntityRenderers.register(StatueBlockEntity.TYPE, StatueRenderer::new);
        BlockEntityRenderers.register(GunPegboardBlockEntity.TYPE, GunPegboardRenderer::new);
        BlockEntityRenderers.register(GunDisplayCaseBlockEntity.TYPE, GunDisplayCaseRenderer::new);
    }
}
