package cn.sh1rocu.tacz.api.event;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public abstract class RenderLivingEvent<T extends LivingEntity, M extends EntityModel<T>> extends BaseEvent {
    private final LivingEntity entity;
    private final LivingEntityRenderer<T, M> renderer;
    private final float partialTick;
    private final PoseStack poseStack;
    private final MultiBufferSource multiBufferSource;
    private final int packedLight;

    public static final Event<PostCallback> POST = EventFactory.createArrayBacked(PostCallback.class, callbacks -> event -> {
        for (PostCallback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface PostCallback {
        void post(Post<?, ?> event);
    }

    protected RenderLivingEvent(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack,
                                MultiBufferSource multiBufferSource, int packedLight) {
        this.entity = entity;
        this.renderer = renderer;
        this.partialTick = partialTick;
        this.poseStack = poseStack;
        this.multiBufferSource = multiBufferSource;
        this.packedLight = packedLight;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public LivingEntityRenderer<T, M> getRenderer() {
        return renderer;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public MultiBufferSource getMultiBufferSource() {
        return multiBufferSource;
    }

    public int getPackedLight() {
        return packedLight;
    }

    public static class Post<T extends LivingEntity, M extends EntityModel<T>> extends RenderLivingEvent<T, M> {
        public Post(LivingEntity entity, LivingEntityRenderer<T, M> renderer, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
            super(entity, renderer, partialTick, poseStack, multiBufferSource, packedLight);
        }
    }
}
