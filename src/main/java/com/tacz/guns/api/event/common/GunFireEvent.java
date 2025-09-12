package com.tacz.guns.api.event.common;

import cn.sh1rocu.tacz.api.LogicalSide;
import cn.sh1rocu.tacz.api.event.BaseEvent;
import cn.sh1rocu.tacz.api.event.ICancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 生物的枪击发的事件。与 {@link GunShootEvent}不同的是，扣动一次扳机可能多次触发这个事件（如枪械处于 Burst 模式），但 {@link GunShootEvent} 只会触发一次
 */
public class GunFireEvent extends BaseEvent implements KubeJSGunEventPoster<GunFireEvent>, ICancellableEvent {
    private final LivingEntity shooter;
    private final ItemStack gunItemStack;
    private final LogicalSide logicalSide;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            try {
                callback.post(event);
            } catch (AbstractMethodError e) {
                // 兼容 YSM 等 mod 注册的 callback 不兼容时，避免崩溃，直接跳过
                System.err.println("[TACZ-R] 跳过不兼容的 GunFireEvent.Callback: " + callback.getClass() + ", " + e);
            } catch (Throwable t) {
                // 其他异常
                t.printStackTrace();
            }
        }
    });

    // 兼容 YSM: 添加EVENT字段
    public static final Event<Callback> EVENT = CALLBACK;

    public interface Callback {
        void post(GunFireEvent event);
    }

    public GunFireEvent(LivingEntity shooter, ItemStack gunItemStack, LogicalSide side) {
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
