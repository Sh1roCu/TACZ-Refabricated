package com.tacz.guns.client.compat;

import com.tacz.guns.api.client.IRecordingCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

/**
 * Utility for resolving the "view player" used during recording/replay playback.
 *
 * <p>If a recording-compat provider has been registered (e.g. by Flashback), its
 * {@link IRecordingCompat#getViewPlayer()} is returned. Otherwise, falls back to
 * {@code Minecraft.getInstance().player}.
 */
public final class RecordingCompatHelper {
    private RecordingCompatHelper() {}

    /**
     * Returns the player whose perspective should be used for gun rendering.
     * Never returns {@code null} -- falls back to the local player.
     */
    @Nonnull
    public static Player getViewPlayer() {
        IRecordingCompat provider = IRecordingCompat.getRegisteredProvider();
        if (provider != null) {
            Player viewPlayer = provider.getViewPlayer();
            if (viewPlayer != null) {
                return viewPlayer;
            }
        }
        return Minecraft.getInstance().player;
    }
}
