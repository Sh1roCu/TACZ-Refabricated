package cn.sh1rocu.tacz.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import org.jetbrains.annotations.Nullable;

import static cn.sh1rocu.tacz.TaCZFabric.*;

public abstract class ClientPlayerNetworkEvent extends BaseEvent {
    private final MultiPlayerGameMode multiPlayerGameMode;
    private final LocalPlayer player;
    private final Connection connection;

    public static final Event<LoggingOutCallback> LOGGING_OUT = EventFactory.createWithPhases(LoggingOutCallback.class, callbacks -> event -> {
        for (LoggingOutCallback callback : callbacks) {
            callback.post(event);
        }
    }, HIGHEST, HIGH, Event.DEFAULT_PHASE, LOW, LOWEST);

    public static final Event<CloneCallback> CLONE = EventFactory.createArrayBacked(CloneCallback.class, callbacks -> event -> {
        for (CloneCallback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface LoggingOutCallback {
        void post(LoggingOut event);
    }

    public interface CloneCallback {
        void post(Clone event);
    }

    protected ClientPlayerNetworkEvent(final MultiPlayerGameMode multiPlayerGameMode, final LocalPlayer player, final Connection connection) {
        this.multiPlayerGameMode = multiPlayerGameMode;
        this.player = player;
        this.connection = connection;
    }

    public MultiPlayerGameMode getMultiPlayerGameMode() {
        return multiPlayerGameMode;
    }

    public LocalPlayer getPlayer() {
        return player;
    }

    public Connection getConnection() {
        return connection;
    }

    public static class LoggingOut extends ClientPlayerNetworkEvent {
        public LoggingOut(@Nullable final MultiPlayerGameMode controller, @Nullable final LocalPlayer player, @Nullable final Connection networkManager) {
            super(controller, player, networkManager);
        }

        @Nullable
        @Override
        public MultiPlayerGameMode getMultiPlayerGameMode() {
            return super.getMultiPlayerGameMode();
        }

        @Nullable
        @Override
        public LocalPlayer getPlayer() {
            return super.getPlayer();
        }

        @Nullable
        @Override
        public Connection getConnection() {
            return super.getConnection();
        }
    }

    public static class Clone extends ClientPlayerNetworkEvent {
        private final LocalPlayer oldPlayer;

        public Clone(final MultiPlayerGameMode pc, final LocalPlayer oldPlayer, final LocalPlayer newPlayer, final Connection networkManager) {
            super(pc, newPlayer, networkManager);
            this.oldPlayer = oldPlayer;
        }

        public LocalPlayer getOldPlayer() {
            return oldPlayer;
        }

        public LocalPlayer getNewPlayer() {
            return super.getPlayer();
        }

        @Override
        public LocalPlayer getPlayer() {
            return super.getPlayer();
        }
    }
}
