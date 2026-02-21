package cn.sh1rocu.tacz.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class RenderTickEvent extends BaseEvent {
    private final Minecraft client;
    public final Phase phase;
    private final DeltaTracker timer;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public RenderTickEvent(Minecraft client, Phase phase, DeltaTracker timer) {
        this.client = client;
        this.phase = phase;
        this.timer = timer;
    }

    public Minecraft getClient() {
        return client;
    }

    public DeltaTracker getTimer() {
        return timer;
    }

    public interface Callback {
        void post(RenderTickEvent event);
    }

    public enum Phase {
        START, END;
    }
}