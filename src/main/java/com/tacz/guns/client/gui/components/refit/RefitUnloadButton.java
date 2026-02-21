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

public class RefitUnloadButton extends Button implements IComponentTooltip {
    public RefitUnloadButton(int pX, int pY, Button.OnPress pOnPress) {
        super(pX, pY, 8, 8, Component.empty(), pOnPress, DEFAULT_NARRATION);
    }

    @Override
    public void renderContents(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        GlStateManager._disableDepthTest();
        GlStateManager._enableBlend();

        int x = getX(), y = getY();
        if (isHoveredOrFocused()) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.UNLOAD_TEXTURE, x, y, 0f, 0f, width, height, 80, 80, 160, 80);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.UNLOAD_TEXTURE, x, y, 80f, 0f, width, height, 80, 80, 160, 80);
        }

        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();
    }

    @Override
    public void renderTooltip(Consumer<List<Component>> consumer) {
        if (this.isHoveredOrFocused()) {
            consumer.accept(Collections.singletonList(Component.translatable("tooltip.tacz.refit.unload")));
        }
    }
}
