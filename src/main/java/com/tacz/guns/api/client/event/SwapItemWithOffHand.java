package com.tacz.guns.api.client.event;

import cn.sh1rocu.tacz.api.event.BaseEvent;
import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 玩家交换主副手物品时触发该事件
 */
public class SwapItemWithOffHand extends BaseEvent implements KubeJSGunEventPoster<SwapItemWithOffHand> {
    public SwapItemWithOffHand() {
        postClientEventToKubeJS(this);
    }

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface Callback {
        void post(SwapItemWithOffHand event);
    }
}
