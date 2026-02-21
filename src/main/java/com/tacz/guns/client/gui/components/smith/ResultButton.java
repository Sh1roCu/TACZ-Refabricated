package com.tacz.guns.client.gui.components.smith;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.tacz.guns.GunMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ResultButton extends Button {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "textures/gui/gun_smith_table.png");
    private final ItemStack stack;
    private boolean isSelected = false;

    public ResultButton(int pX, int pY, ItemStack stack, Button.OnPress onPress) {
        super(pX, pY, 94, 16, Component.empty(), onPress, DEFAULT_NARRATION);
        this.stack = stack;
    }

    @Override
    public void renderContents(@NotNull GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        GlStateManager._enableDepthTest();

        if (isSelected) {
            if (isHoveredOrFocused()) {
                gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX() - 1, this.getY() - 1, 52f, 229f, this.width + 2, this.height + 2, 256, 256);
            } else {
                gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 53f, 230f, this.width, this.height, 256, 256);
            }
        } else {
            if (isHoveredOrFocused()) {
                gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX() - 1, this.getY() - 1, 52f, 211f, this.width + 2, this.height + 2, 256, 256);
            } else {
                gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 53f, 212f, this.width, this.height, 256, 256);
            }
        }
        Minecraft mc = Minecraft.getInstance();
        gui.renderItem(stack, this.getX() + 1, this.getY());

        Component hoverName = this.stack.getHoverName();
        gui.drawString(mc.font, hoverName, this.getX() + 20, this.getY() + 4, 0xFFFFFF, false);
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        this.isSelected = true;
        this.onPress.onPress(this);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void renderTooltips(Consumer<ItemStack> consumer) {
        if (this.isHoveredOrFocused() && !this.stack.isEmpty()) {
            consumer.accept(this.stack);
        }
    }
}
