package com.tacz.guns.api.client.event;

import cn.sh1rocu.tacz.api.event.BaseEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.api.event.common.KubeJSGunEventPoster;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * 在调用 ItemInHandRenderer#renderHandsWithItems 方法时触发该事件
 * 用于相机动画相关调用
 */
public class BeforeRenderHandEvent extends BaseEvent implements KubeJSGunEventPoster<BeforeRenderHandEvent> {
    private final PoseStack poseStack;

    public static Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface Callback {
        void post(BeforeRenderHandEvent event);
    }

    public BeforeRenderHandEvent(PoseStack poseStack) {
        this.poseStack = poseStack;
        postClientEventToKubeJS(this);
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
