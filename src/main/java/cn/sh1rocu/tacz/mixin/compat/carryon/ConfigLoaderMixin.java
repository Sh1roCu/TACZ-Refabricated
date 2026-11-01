package cn.sh1rocu.tacz.mixin.compat.carryon;

import com.tacz.guns.compat.carryon.BlackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.config.ConfigLoader;

@Mixin(ConfigLoader.class)
public class ConfigLoaderMixin {
    @Inject(remap = false, method = "onConfigLoaded", at = @At("TAIL"))
    private static void onConfigLoaded(CallbackInfo ci) {
        BlackList.addBlackList();
    }
}
