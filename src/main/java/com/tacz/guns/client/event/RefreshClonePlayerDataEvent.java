package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.ClientPlayerNetworkEvent;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.util.DelayedTask;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.function.BooleanSupplier;

/**
 * 当玩家跨越维度时，客户端需要刷新一次玩家的配件属性缓存
 */
@Environment(EnvType.CLIENT)
public class RefreshClonePlayerDataEvent {
    public static void onClientPlayerClone(ClientPlayerNetworkEvent.Clone event) {
        LocalPlayer newPlayer = event.getNewPlayer();
        // 但是这个事件触发时，玩家的背包并未同步，导致无法读取枪械数据进行配件属性缓存的刷新
        // 延迟 10 tick 执行缓存刷新就好了
        DelayedTask.add(() -> IGunOperator.fromLivingEntity(newPlayer).initialData(), 10);
    }

    /**
     * 延迟执行是通过这个方法执行的
     */
    public static void onClientTick(Minecraft client) {
        try {
            DelayedTask.SUPPLIERS.removeIf(BooleanSupplier::getAsBoolean);
        } catch (Exception e) {
            DelayedTask.SUPPLIERS.clear();
            GunMod.LOGGER.error(e.getMessage(), e);
        }
    }
}
