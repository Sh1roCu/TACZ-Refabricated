package cn.sh1rocu.tacz.mixin.common.accessor;

import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;

@Mixin(BlockableEventLoop.class)
public interface BlockableEventLoopAccessor {
    @Invoker("submitAsync")
    CompletableFuture<Void> tacz$submitAsync(Runnable task);
}
