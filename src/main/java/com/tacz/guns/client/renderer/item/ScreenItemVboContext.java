package com.tacz.guns.client.renderer.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/**
 * Tracks the actual screen draw, which occurs after any visible world has been rendered.
 */
@Environment(EnvType.CLIENT)
public final class ScreenItemVboContext {
    private static int depth;

    private ScreenItemVboContext() {
    }

    public static boolean isActive() {
        return depth > 0;
    }

    public static void beforeScreen(Screen screen, GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {
        depth++;
    }

    // ForgeHooksClient posts Render.Post even when Render.Pre cancels the screen body.
    public static void afterScreen(Screen screen, GuiGraphics drawContext, int mouseX, int mouseY, float tickDelta) {
        if (depth > 0) depth--;
    }
}
