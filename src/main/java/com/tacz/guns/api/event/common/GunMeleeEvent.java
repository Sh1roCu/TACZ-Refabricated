package com.tacz.guns.api.event.common;

import cn.sh1rocu.tacz.api.LogicalSide;
import cn.sh1rocu.tacz.api.event.BaseEvent;
import cn.sh1rocu.tacz.api.event.ICancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 用枪近战时触发
 */
public class GunMeleeEvent extends BaseEvent implements KubeJSGunEventPoster<GunMeleeEvent>, ICancellableEvent {
    private final LivingEntity shooter;
    private final ItemStack gunItemStack;
    private final LogicalSide logicalSide;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            try {
                callback.post(event);
            } catch (AbstractMethodError e) {
                // 兼容 YSM 等 mod 注册的 callback 不兼容时，避免崩溃，直接跳过
                System.err.println("[TACZ] 跳过不兼容的GunMeleeEvent.Callback: " + callback.getClass() + ", " + e);
            } catch (Throwable t) {
                // 其他异常
                t.printStackTrace();
            }
        }
    });

    // 兼容 YSM: 添加EVENT字段
    public static final Event<Callback> EVENT = CALLBACK;

    public interface Callback {
        void post(GunMeleeEvent event);
    }

    public GunMeleeEvent(LivingEntity shooter, ItemStack gunItemStack, LogicalSide side) {
        this.shooter = shooter;
        this.gunItemStack = gunItemStack;
        this.logicalSide = side;
        postEventToKubeJS(this);
    }

    public LivingEntity getShooter() {
        return shooter;
    }

    public ItemStack getGunItemStack() {
        return gunItemStack;
    }

    public LogicalSide getLogicalSide() {
        return logicalSide;
    }
}
