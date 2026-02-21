package com.tacz.guns.client.gui.components.smith;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.tacz.guns.GunMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TypeButton extends Button {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "textures/gui/gun_smith_table.png");
    private final ItemStack stack;
    private boolean isSelected = false;

    public TypeButton(int pX, int pY, ItemStack stack, Button.OnPress onPress) {
        super(pX, pY, 24, 25, Component.empty(), onPress, DEFAULT_NARRATION);
        this.stack = stack;
    }

    @Override
    public void renderContents(@NotNull GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        GlStateManager._enableDepthTest();

        int vOffset = isHoveredOrFocused() ? 204 + this.height : 204;
        if (isSelected) {
            gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 0f, (float) vOffset, this.width, this.height, 256, 256);
        } else {
            gui.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, TEXTURE, this.getX(), this.getY(), 26f, (float) vOffset, this.width, this.height, 256, 256);
        }

        gui.renderItem(this.stack, this.getX() + 4, this.getY() + 5);
    }

    @Override
    public void onPress(net.minecraft.client.input.InputWithModifiers input) {
        this.isSelected = true;
        this.onPress.onPress(this);
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
