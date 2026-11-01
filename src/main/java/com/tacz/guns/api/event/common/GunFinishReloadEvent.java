package com.tacz.guns.api.event.common;

import cn.sh1rocu.tacz.api.LogicalSide;
import cn.sh1rocu.tacz.api.event.BaseEvent;
import cn.sh1rocu.tacz.api.event.ICancellableEvent;
import net.minecraft.world.item.ItemStack;

/**
 * 生物结束更换枪械弹药时触发的事件。
 */
public class GunFinishReloadEvent extends BaseEvent implements KubeJSGunEventPoster<GunFinishReloadEvent>, ICancellableEvent {
    private final ItemStack gunItemStack;
    private final LogicalSide logicalSide;

    public GunFinishReloadEvent(ItemStack gunItemStack, LogicalSide side) {
        this.gunItemStack = gunItemStack;
        this.logicalSide = side;
        postEventToKubeJS(this);
    }

    public ItemStack getGunItemStack() {
        return gunItemStack;
    }

    public LogicalSide getLogicalSide() {
        return logicalSide;
    }
}
