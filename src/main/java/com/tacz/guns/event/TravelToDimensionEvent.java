package com.tacz.guns.event;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 修正跨纬度时，枪械数据不刷新的问题，这是服务端的刷新
 */
public class TravelToDimensionEvent {
    /// For all Entities which are not Players
    public static void onTravelToDimension(Entity originalEntity, Entity newEntity, ServerLevel origin, ServerLevel destination) {
        if (newEntity instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().getItem() instanceof IGun) {
            IGunOperator.fromLivingEntity(livingEntity).initialData();
        }
    }

    /// Only for Players
    public static void onTravelToDimension(ServerPlayer player, ServerLevel origin, ServerLevel destination) {
        if (player.getMainHandItem().getItem() instanceof IGun) {
            IGunOperator.fromLivingEntity(player).initialData();
        }
    }
}
