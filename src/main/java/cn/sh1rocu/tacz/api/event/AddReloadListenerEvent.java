package cn.sh1rocu.tacz.api.event;

import com.google.common.collect.ImmutableList;
import com.tacz.guns.GunMod;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AddReloadListenerEvent extends BaseEvent {
    private final List<PreparableReloadListener> listeners = new ArrayList<>();
    private final ReloadableServerResources serverResources;
    private final RegistryAccess registryAccess;

    public static Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> ((event) -> {
        for (Callback e : callbacks) {
            e.post(event);
        }
    }));

    public interface Callback {
        void post(AddReloadListenerEvent event);
    }

    public AddReloadListenerEvent(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        this.serverResources = serverResources;
        this.registryAccess = registryAccess;
    }

    public ReloadableServerResources getServerResources() {
        return serverResources;
    }

    public RegistryAccess getRegistryAccess() {
        return registryAccess;
    }

    public void addListener(PreparableReloadListener listener) {
        listeners.add(new WrappedStateAwareListener(listener));
    }

    public List<PreparableReloadListener> getListeners() {
        return ImmutableList.copyOf(listeners);
    }

    private static class WrappedStateAwareListener implements PreparableReloadListener {
        private final PreparableReloadListener wrapped;

        private WrappedStateAwareListener(final PreparableReloadListener wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public CompletableFuture<Void> reload(final SharedState sharedState, final Executor backgroundExecutor, final PreparationBarrier stage, final Executor gameExecutor) {
            if (FabricLoader.getInstance().isModLoaded(GunMod.MOD_ID))
                return wrapped.reload(sharedState, backgroundExecutor, stage, gameExecutor);
            else
                return CompletableFuture.completedFuture(null);
        }
    }
}