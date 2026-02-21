package cn.sh1rocu.tacz.util.forge;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class ImageButton extends Button {
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final Identifier resourceLocation;
    private final Button.OnPress onPressCallback;

    public ImageButton(int pX, int pY, int pWidth, int pHeight, int pXTexStart, int pYTexStart, int pYDiffTex, Identifier pIdentifier, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, CommonComponents.EMPTY, pOnPress, DEFAULT_NARRATION);
        xTexStart = pXTexStart;
        yTexStart = pYTexStart;
        yDiffTex = pYDiffTex;
        resourceLocation = pIdentifier;
        onPressCallback = pOnPress;
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        onPressCallback.onPress(this);
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderTexture(gui, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, 256, 256);
    }

    private void renderTexture(GuiGraphics pGuiGraphics, Identifier pTexture, int pX, int pY, int pUOffset, int pVOffset, int pTextureDifference, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        int i = pVOffset;
        if (!this.isActive()) {
            i = pVOffset + pTextureDifference * 2;
        } else if (this.isHoveredOrFocused()) {
            i = pVOffset + pTextureDifference;
        }

        pGuiGraphics.blit(RenderPipelines.GUI_TEXTURED, pTexture, pX, pY, (float) pUOffset, (float) i, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }
}
