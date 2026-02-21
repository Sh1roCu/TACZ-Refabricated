package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.util.forge.ClientHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractPlayerMixin extends Player {
    public AbstractPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @WrapOperation(method = "getFieldOfViewModifier(ZF)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"))
    private float tacz$getForgeFovModifier(float delta, float start, float end, Operation<Float> original) {
        return ClientHooks.getFieldOfViewModifier(this, end);
    }
}
