package cn.sh1rocu.tacz.mixin.client.accessor;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
    @Accessor("SLIDER_LOCATION")
    static ResourceLocation tacz$getSliderLocation() {
        throw new AssertionError();
    }

    @Invoker("getTextureY")
    int tacz$getTextureY();

    @Invoker("getHandleTextureY")
    int tacz$getHandleTextureY();
}
