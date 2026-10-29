package com.tacz.guns.client.gui.overlay;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;

public class DisplayCaseLockHintOverlay implements LayeredDraw.Layer {
    private static final int DISPLAY_TICKS = 40;
    private static final int Y_OFFSET = 32;
    private static Component message = Component.empty();
    private static int ticksRemaining;

    public static final DisplayCaseLockHintOverlay INSTANCE = new DisplayCaseLockHintOverlay();

    public static void show(Component component) {
        message = component;
        ticksRemaining = DISPLAY_TICKS;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        if (ticksRemaining <= 0) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        graphics.drawString(font, message, (int) ((width - font.width(message)) / 2.0F), (int) (height / 2.0F + Y_OFFSET), ChatFormatting.YELLOW.getColor(), false);
    }

    public static void onClientTick(Minecraft client) {
        if (/*event.phase == TickEvent.Phase.END &&*/ ticksRemaining > 0) {
            ticksRemaining--;
        }
    }
}
