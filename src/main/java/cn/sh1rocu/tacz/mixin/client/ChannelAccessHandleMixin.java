package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.api.event.PlaySoundSourceEvent;
import cn.sh1rocu.tacz.api.mixin.ChannelAccessHandleInjection;
import cn.sh1rocu.tacz.util.SoundConsumerStorage;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

// From kilt
@Mixin(ChannelAccess.ChannelHandle.class)
public abstract class ChannelAccessHandleMixin implements ChannelAccessHandleInjection {
    @Shadow
    @Nullable Channel channel;
    @Unique
    private Library.Pool tacz$pool;
    @Unique
    private SoundEngine tacz$soundEngine;
    @Unique
    private SoundInstance tacz$soundInstance;

    @Override
    public void tacz$setPool(Library.Pool pool) {
        this.tacz$pool = pool;
    }

    @Override
    public void tacz$setSoundEngine(SoundEngine engine) {
        this.tacz$soundEngine = engine;
    }

    @Override
    public void tacz$setSoundInstance(SoundInstance instance) {
        this.tacz$soundInstance = instance;
    }

    @Inject(method = "method_19737", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V", shift = At.Shift.AFTER))
    private void tacz$callPlaySoundEvents(Consumer<Channel> consumer, CallbackInfo ci) {
        if (this.channel != null && tacz$soundEngine != null && tacz$soundInstance != null && SoundConsumerStorage.soundConsumerChannels.remove(consumer)) {
            if (tacz$pool == Library.Pool.STATIC) {
                PlaySoundSourceEvent.CALLBACK.invoker().post(new PlaySoundSourceEvent(tacz$soundEngine, tacz$soundInstance, this.channel));
            }
            // 暂时用不到
            /* else if (tacz$pool == Library.Pool.STREAMING) {
                PlayStreamingSourceEvent.CALLBACK.invoker().post(new PlayStreamingSourceEvent(tacz$soundEngine, tacz$soundInstance, this.channel));
            }*/
        }
    }
}