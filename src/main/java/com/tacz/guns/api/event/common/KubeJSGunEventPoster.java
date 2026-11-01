package com.tacz.guns.api.event.common;

import cn.sh1rocu.tacz.api.event.BaseEvent;
import com.tacz.guns.compat.kubejs.events.TimelessClientEvents;
import com.tacz.guns.compat.kubejs.events.TimelessCommonEvents;
import com.tacz.guns.compat.kubejs.events.TimelessServerEvents;
import net.fabricmc.loader.api.FabricLoader;

public interface KubeJSGunEventPoster<E extends BaseEvent> {
    default void postEventToKubeJS(E event) {
        if (FabricLoader.getInstance().isModLoaded("kubejs")) {
            TimelessCommonEvents.INSTANCE.postKubeJSEvent(event);
        }
    }

    //客户端事件应调用此方法
    default void postClientEventToKubeJS(E event) {
        if (FabricLoader.getInstance().isModLoaded("kubejs")) {
            TimelessClientEvents.INSTANCE.postKubeJSEvent(event);
        }
    }

    //服务端事件应调用此方法
    default void postServerEventToKubeJS(E event) {
        if (FabricLoader.getInstance().isModLoaded("kubejs")) {
            TimelessServerEvents.INSTANCE.postKubeJSEvent(event);
        }
    }
}
