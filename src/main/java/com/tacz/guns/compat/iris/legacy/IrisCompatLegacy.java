package com.tacz.guns.compat.iris.legacy;

import net.coderbot.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.coderbot.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;

public final class IrisCompatLegacy {
    public static boolean isRenderShadow() {
        return ShadowRenderingState.areShadowsCurrentlyBeingRendered();
    }

    public static boolean endBatch(MultiBufferSource.BufferSource bufferSource) {
        if (bufferSource instanceof FullyBufferedMultiBufferSource fullyBufferedMultiBufferSource) {
            fullyBufferedMultiBufferSource.endBatch();
            return true;
        }
        return false;
    }

}
