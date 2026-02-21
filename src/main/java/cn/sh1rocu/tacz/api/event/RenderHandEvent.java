package cn.sh1rocu.tacz.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class RenderHandEvent extends BaseEvent implements ICancellableEvent {
    private final AbstractClientPlayer player;
    private final InteractionHand hand;
    private final ItemStack stack;
    private final PoseStack matrices;
    private final SubmitNodeCollector collector;
    private final float tickDelta;
    private final float pitch;
    private final float swingProgress;
    private final float equipProgress;
    private final int light;

    public static final Event<Callback> CALLBACK = EventFactory.createArrayBacked(Callback.class, callbacks -> (handEvent) -> {
        for (Callback callback : callbacks) {
            callback.post(handEvent);
        }
    });

    public RenderHandEvent(AbstractClientPlayer player, InteractionHand hand, ItemStack stack, PoseStack matrices, SubmitNodeCollector collector, float tickDelta, float pitch, float swingProgress, float equipProgress, int light) {
        this.player = player;
        this.hand = hand;
        this.stack = stack;
        this.matrices = matrices;
        this.collector = collector;
        this.tickDelta = tickDelta;
        this.pitch = pitch;
        this.swingProgress = swingProgress;
        this.equipProgress = equipProgress;
        this.light = light;
    }

    public AbstractClientPlayer getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    public PoseStack getPoseStack() {
        return matrices;
    }

    public SubmitNodeCollector getCollector() {
        return collector;
    }

    public int getPackedLight() {
        return light;
    }

    public float getPartialTick() {
        return tickDelta;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public float getPitch() {
        return pitch;
    }

    public float getEquipProgress() {
        return equipProgress;
    }

    public float getSwingProgress() {
        return swingProgress;
    }

    public interface Callback {
        void post(RenderHandEvent event);
    }
}