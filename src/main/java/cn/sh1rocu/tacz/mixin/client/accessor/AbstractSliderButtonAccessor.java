package cn.sh1rocu.tacz.mixin.client.accessor;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
    @Invoker("getSprite")
    ResourceLocation tacz$getSprite();

    @Invoker("getHandleSprite")
    ResourceLocation tacz$getHandleSprite();
}
