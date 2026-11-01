package com.tacz.guns.compat.iris.newly;

import net.irisshaders.batchedentityrendering.impl.FullyBufferedMultiBufferSource;
import net.irisshaders.iris.shadows.ShadowRenderingState;
import net.minecraft.client.renderer.MultiBufferSource;

public final class IrisCompatNewly {
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
