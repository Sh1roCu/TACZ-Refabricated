package cn.sh1rocu.tacz.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public class RenderTickEvent extends BaseEvent {
    private final Minecraft client;
    public final Phase phase;
    public final float renderTickTime;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
        for (Callback callback : callbacks) {
            callback.post(event);
        }
    });

    public RenderTickEvent(Minecraft client, Phase phase, float renderTickTime) {
        this.client = client;
        this.phase = phase;
        this.renderTickTime = renderTickTime;
    }

    public Minecraft getClient() {
        return client;
    }

    public interface Callback {
        void post(RenderTickEvent event);
    }

    public enum Phase {
        START, END;
    }
}