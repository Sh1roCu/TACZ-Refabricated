package com.tacz.guns.api.client;

import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

/**
 * Interface for recording/replay mods to provide the "view player" during playback.
 *
 * <p>When a recording mod (e.g. Flashback) is playing back a replay, it needs TACZ to
 * render guns from the perspective of the spectated player rather than
 * {@code Minecraft.getInstance().player}. Recording mods register a provider of this
 * interface at startup via {@link #register(IRecordingCompat)}.
 */
public interface IRecordingCompat {
    /**
     * Returns the player whose perspective should be used for gun rendering, or
     * {@code null} to indicate that the regular local player should be used.
     */
    @Nullable
    Player getViewPlayer();

    /* ---- static registry ---- */

    IRecordingCompat.ProviderHolder HOLDER = new IRecordingCompat.ProviderHolder();

    final class ProviderHolder {
        @Nullable
        public IRecordingCompat registeredProvider = null;
    }

    /**
     * Registers a recording-compat provider. Only the last registered provider is used.
     * This method is intended to be called once during mod initialization.
     */
    static void register(IRecordingCompat provider) {
        HOLDER.registeredProvider = provider;
    }

    /**
     * Returns the currently registered provider, or {@code null} if none is registered.
     */
    @Nullable
    static IRecordingCompat getRegisteredProvider() {
        return HOLDER.registeredProvider;
    }
}
