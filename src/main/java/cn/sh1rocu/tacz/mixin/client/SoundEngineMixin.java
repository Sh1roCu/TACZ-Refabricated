package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.mixin.ChannelAccessHandleInjection;
import cn.sh1rocu.tacz.util.SoundConsumerStorage;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

// From Kilt
@Environment(EnvType.CLIENT)
@Mixin(SoundEngine.class)
public abstract class SoundEngineMixin {
    @Inject(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void tacz$prepareChannelInfo(SoundInstance soundInstance, CallbackInfoReturnable<SoundEngine.PlayResult> cir, @Local ChannelAccess.ChannelHandle channelHandle, @Local Sound sound) {
        var injection = ((ChannelAccessHandleInjection) channelHandle);

        if (sound.shouldStream())
            injection.tacz$setPool(Library.Pool.STREAMING);
        else
            injection.tacz$setPool(Library.Pool.STATIC);

        injection.tacz$setSoundInstance(soundInstance);
        injection.tacz$setSoundEngine((SoundEngine) (Object) this);
    }

    @ModifyArg(method = "method_19757", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V"))
    private static Consumer<Channel> tacz$storeSourceConsumer(Consumer<Channel> consumer) {
        SoundConsumerStorage.soundConsumerChannels.add(consumer);
        return consumer;
    }

    // 暂时用不到
/*    @ModifyArg(method = "method_19758", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sounds/ChannelAccess$ChannelHandle;execute(Ljava/util/function/Consumer;)V"))
    private static Consumer<Channel> tacz$storeStreamConsumer(Consumer<Channel> consumer) {
        SoundConsumerStorage.soundConsumerChannels.add(consumer);
        return consumer;
    }*/
}
