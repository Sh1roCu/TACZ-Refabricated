package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.api.event.PlayerTickEvent;
import cn.sh1rocu.tacz.api.mixin.ForcePoseInjection;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin implements ForcePoseInjection {
    @Unique
    private Pose tacz$forcedPose;

    @Override
    public Pose tacz$getForcedPose() {
        return tacz$forcedPose;
    }

    @Override
    public void tacz$setForcedPose(Pose pose) {
        tacz$forcedPose = pose;
    }

    @Inject(method = "updatePlayerPose()V", at = @At("HEAD"), cancellable = true)
    public void tacz$updatePlayerPose(CallbackInfo ci) {
        if (tacz$forcedPose != null) {
            ((Player) (Object) this).setPose(tacz$forcedPose);
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tacz$tickStartEvent(CallbackInfo ci) {
        PlayerTickEvent.START.invoker().onStart(new PlayerTickEvent.Pre((Player) (Object) this));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tacz$tickEndEvent(CallbackInfo ci) {
        PlayerTickEvent.END.invoker().onEnd(new PlayerTickEvent.Post((Player) (Object) this));
    }
}
