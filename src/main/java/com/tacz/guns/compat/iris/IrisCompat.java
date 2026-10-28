package com.tacz.guns.compat.iris;

import com.tacz.guns.compat.iris.legacy.IrisCompatLegacy;
import com.tacz.guns.compat.iris.newly.IrisCompatNewly;
import com.tacz.guns.compat.oculus.IUnflushableWrapper;
import com.tacz.guns.init.CompatRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.renderer.MultiBufferSource;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class IrisCompat {
    private static final Version VERSION;

    static {
        try {
            VERSION = Version.parse("1.7.0");
        } catch (VersionParsingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Function<MultiBufferSource, Boolean> END_BATCH_FUNCTION;
    private static Supplier<Boolean> IS_RENDER_SHADOW_SUPPER;
    private static Supplier<MultiBufferSource> HAND_BUFFER_SOURCE;

    public static void initCompat() {
        FabricLoader.getInstance().getModContainer(CompatRegistry.IRIS).ifPresent(mod -> {
            if (mod.getMetadata().getVersion().compareTo(VERSION) >= 0) {
                END_BATCH_FUNCTION = IrisCompatNewly::endBatch;
                IS_RENDER_SHADOW_SUPPER = IrisCompatNewly::isRenderShadow;
                HAND_BUFFER_SOURCE = IrisCompatNewly::getHandBufferSource;
            } else {
                END_BATCH_FUNCTION = IrisCompatLegacy::endBatch;
                IS_RENDER_SHADOW_SUPPER = IrisCompatLegacy::isRenderShadow;
                HAND_BUFFER_SOURCE = IrisCompatLegacy::getHandBufferSource;
            }
        });
    }

    public static boolean isRenderShadow() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS)) {
            return IS_RENDER_SHADOW_SUPPER.get();
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
            // Iris 第一人称手部 pass 传入的是 UnflushableWrapper，其 endBatch 是空操作；
            // 经 mixin 注入 IUnflushableWrapper 后取出内部真正的 FullyBufferedMultiBufferSource，
            // 再交给版本分派 flush（保证模板状态与实际绘制同步）
            if (bufferSource instanceof IUnflushableWrapper wrapper) {
                bufferSource = wrapper.tacz$getWrapped();
            }
            return END_BATCH_FUNCTION.apply(bufferSource);
        }
        return false;
    }

    /**
     * Compatibility fallback for render entry points without an explicit buffer.
     */
    @Nullable
    public static MultiBufferSource getHandBufferSource() {
        return FabricLoader.getInstance().isModLoaded(CompatRegistry.IRIS) && HAND_BUFFER_SOURCE != null
                ? HAND_BUFFER_SOURCE.get() : null;
    }
}
