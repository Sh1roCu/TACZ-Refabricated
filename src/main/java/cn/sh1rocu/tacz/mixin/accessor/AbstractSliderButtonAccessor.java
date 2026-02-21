package cn.sh1rocu.tacz.mixin.accessor;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSliderButton.class)
public interface AbstractSliderButtonAccessor {
    @Invoker("getSprite")
    Identifier tacz$getSprite();

    @Invoker("getHandleSprite")
    Identifier tacz$getHandleSprite();
}
