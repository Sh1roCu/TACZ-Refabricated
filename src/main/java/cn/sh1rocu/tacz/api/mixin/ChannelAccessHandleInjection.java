package cn.sh1rocu.tacz.api.mixin;

import com.mojang.blaze3d.audio.Library;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

// From Kilt
public interface ChannelAccessHandleInjection {
    void tacz$setPool(Library.Pool pool);

    void tacz$setSoundInstance(SoundInstance instance);

    void tacz$setSoundEngine(SoundEngine engine);
}