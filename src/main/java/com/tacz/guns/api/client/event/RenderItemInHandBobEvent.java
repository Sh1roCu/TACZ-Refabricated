package com.tacz.guns.api.client.event;

import cn.sh1rocu.tacz.api.event.BaseEvent;
import cn.sh1rocu.tacz.api.event.ICancellableEvent;
import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 当第一人称视角触发摇晃时，玩家手部的摇晃
 */
public class RenderItemInHandBobEvent extends BaseEvent implements KubeJSGunEventPoster<RenderItemInHandBobEvent> {
    public static final Event<HurtCallback> HURT = EventFactory.createArrayBacked(HurtCallback.class, callbacks -> event -> {
        for (HurtCallback callback : callbacks) {
            callback.post(event);
        }
    });

    public static final Event<ViewCallback> VIEW = EventFactory.createArrayBacked(ViewCallback.class, callbacks -> event -> {
        for (ViewCallback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface HurtCallback {
        void post(BobHurt event);
    }

    public interface ViewCallback {
        void post(BobView event);
    }

    public static class BobHurt extends RenderItemInHandBobEvent implements ICancellableEvent {
        public BobHurt() {
            postClientEventToKubeJS(this);
        }
    }

    public static class BobView extends RenderItemInHandBobEvent implements ICancellableEvent {
        public BobView() {
            postClientEventToKubeJS(this);
        }
    }
}
