package cn.sh1rocu.tacz.api.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.TextureAtlas;

@Environment(EnvType.CLIENT)
public class TextureStitchEvent extends BaseEvent {
    private final TextureAtlas atlas;

    public static final Event<PostCallback> POST = EventFactory.createArrayBacked(PostCallback.class, callbacks -> event -> {
        for (PostCallback callback : callbacks) {
            callback.post(event);
        }
    });

    public interface PostCallback {
        void post(Post event);
    }

    public TextureStitchEvent(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public static class Post extends TextureStitchEvent {
        public Post(TextureAtlas map) {
            super(map);
        }
    }
}
