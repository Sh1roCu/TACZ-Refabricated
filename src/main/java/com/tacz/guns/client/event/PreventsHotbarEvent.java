package com.tacz.guns.client.event;

import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.gui.GunSmithTableScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.concurrent.atomic.AtomicBoolean;

@Environment(EnvType.CLIENT)
public class PreventsHotbarEvent {
    public static void onRenderHotbarEvent(AtomicBoolean cancelled) {
        // todo 需要测试行为
        Screen screen = Minecraft.getInstance().screen;
        // 枪械合成台界面关闭背景
        if (screen instanceof GunSmithTableScreen) {
            cancelled.set(true);
            return;
        }
        // 枪械改装界面关闭背景
        if (screen instanceof GunRefitScreen) {
            cancelled.set(true);
        }
    }
}
