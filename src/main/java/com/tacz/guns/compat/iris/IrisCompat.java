package com.tacz.guns.compat.iris;

import com.tacz.guns.init.CompatRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.MultiBufferSource;

public final class IrisCompat {
    public static void initCompat() {
    }

    public static MultiBufferSource getHandBufferSource() {
        return FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS) ? IrisCompatInner.getHandBufferSource() : null;
    }

    public static boolean isPackInUseQuick() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return IrisCompatInner.isPackInUseQuick();
        }
        return false;
    }

    public static boolean isRenderShadow() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return IrisCompatInner.isRenderShadow();
        }
        return false;
    }

    public static boolean isUsingRenderPack() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return IrisApi.getInstance().isShaderPackInUse();
        }
        return false;
    }

    public static boolean endBatch(MultiBufferSource bufferSource) {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            // Iris 的手部 pass 使用 UnflushableWrapper，其 endBatch 是空操作。
            // 取出实际缓冲，确保模板状态切换前完成绘制。
            if (bufferSource instanceof IUnflushableWrapper wrapper) {
                bufferSource = wrapper.tacz$getWrapped();
            }
            return IrisCompatInner.endBatch(bufferSource);
        }
        return false;
    }
}
