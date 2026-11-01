package cn.sh1rocu.tacz.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class PlayerEvent extends LivingEvent {
    private final Player player;

    public static final Event<PlayerLoggedInCallback> LOGGED_IN = EventFactory.createArrayBacked(PlayerLoggedInCallback.class, callbacks -> event -> {
        for (PlayerLoggedInCallback callback : callbacks) callback.post(event);
    });

    public static final Event<PlayerLoggedOutCallback> LOGGED_OUT = EventFactory.createArrayBacked(PlayerLoggedOutCallback.class, callbacks -> event -> {
        for (PlayerLoggedOutCallback callback : callbacks) callback.post(event);
    });

    public interface PlayerLoggedInCallback {
        void post(PlayerLoggedInEvent event);
    }

    public interface PlayerLoggedOutCallback {
        void post(PlayerLoggedOutEvent event);
    }

    public PlayerEvent(Player player) {
        super(player);
        this.player = player;
    }

    @Override
    public Player getEntity() {
        return player;
    }

    public static class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player) {
            super(player);
        }
    }

    public static class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(Player player) {
            super(player);
        }
    }

}