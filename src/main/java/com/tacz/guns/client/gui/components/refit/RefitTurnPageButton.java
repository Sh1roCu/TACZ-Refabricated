package com.tacz.guns.client.gui.components.refit;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RefitTurnPageButton extends Button implements IComponentTooltip {
    private final boolean isUpPage;

    public RefitTurnPageButton(int pX, int pY, boolean isUpPage, OnPress pOnPress) {
        super(pX, pY, 18, 8, Component.empty(), pOnPress, DEFAULT_NARRATION);
        this.isUpPage = isUpPage;
    }

    @Override
    public void renderContents(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        GlStateManager._disableDepthTest();
        GlStateManager._enableBlend();

        int x = getX(), y = getY();
        int yOffset = isUpPage ? 0 : 80;
        if (isHoveredOrFocused()) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.TURN_PAGE_TEXTURE, x, y, 0f, (float) yOffset, width, height, 180, 80, 180, 160);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.TURN_PAGE_TEXTURE, x + 1, y + 1, 10f, (float) (yOffset + 10), width - 2, height - 2, 180 - 20, 80 - 20, 180, 160);
        }

        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();
    }

    @Override
    public void renderTooltip(Consumer<List<Component>> consumer) {
        if (this.isHoveredOrFocused()) {
            String key = isUpPage ? "tooltip.tacz.page.previous" : "tooltip.tacz.page.next";
            consumer.accept(Collections.singletonList(Component.translatable(key)));
        }
    }
}
