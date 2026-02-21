package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.util.forge.EventHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableResourcesMixin {
    @Unique
    private static ReloadableServerResources tacz$serverResources;

    @Inject(method = "method_58296", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;", shift = At.Shift.BEFORE))
    private static void tacz$loadResources(CallbackInfoReturnable<?> cir, @Local ReloadableServerResources reloadableServerResources) {
        tacz$serverResources = reloadableServerResources;
    }

    @ModifyArg(method = "method_58296", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
    private static List<PreparableReloadListener> tacz$addReloadListener(List<PreparableReloadListener> original, @Local(argsOnly = true) ReloadableServerRegistries.LoadResult loadResult) {
        ArrayList<PreparableReloadListener> listeners = new ArrayList<>(original);
        listeners.addAll(EventHooks.onResourceReload(tacz$serverResources, loadResult.layers().compositeAccess()));
        return listeners;
    }

    @Inject(method = "loadResources", at = @At(value = "TAIL"))
    private static void tacz$finishedLoadResources(ResourceManager resourceManager, LayeredRegistryAccess<RegistryLayer> registries, List<Registry.PendingTags<?>> pendingTags, FeatureFlagSet enabledFeatures, Commands.CommandSelection commandSelection, PermissionSet permissionSet, Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> cir) {
        tacz$serverResources = null;
    }
}
