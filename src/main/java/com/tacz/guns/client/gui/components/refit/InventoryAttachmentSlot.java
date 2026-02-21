package com.tacz.guns.client.gui.components.refit;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.tacz.guns.client.gui.GunRefitScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class InventoryAttachmentSlot extends Button implements IStackTooltip {
    private final int slotIndex;
    private final Inventory inventory;

    public InventoryAttachmentSlot(int pX, int pY, int slotIndex, Inventory inventory, Button.OnPress onPress) {
        super(pX, pY, 18, 18, Component.empty(), onPress, DEFAULT_NARRATION);
        this.slotIndex = slotIndex;
        this.inventory = inventory;
    }

    @Override
    public void renderTooltip(Consumer<ItemStack> consumer) {
        if (this.isHoveredOrFocused() && 0 <= this.slotIndex && this.slotIndex < this.inventory.getContainerSize()) {
            ItemStack item = this.inventory.getItem(slotIndex);
            consumer.accept(item);
        }
    }

    @Override
    public void renderContents(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        GlStateManager._disableDepthTest();
        GlStateManager._enableBlend();

        int x = getX(), y = getY();
        if (isHoveredOrFocused()) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.SLOT_TEXTURE, x, y, 0f, 0f, width, height, 18, 18);
        } else {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, GunRefitScreen.SLOT_TEXTURE, x + 1, y + 1, 1f, 1f, width - 2, height - 2, 18, 18);
        }
        graphics.renderItem(inventory.getItem(slotIndex), x + 1, y + 1);

        GlStateManager._enableDepthTest();
        GlStateManager._disableBlend();
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}
