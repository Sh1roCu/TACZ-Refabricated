package com.tacz.guns.client.particle;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.particles.BulletHoleOption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Author: Forked from MrCrayfish, continued by Timeless devs
 * <p>
 * TODO: In 1.21.11, the particle rendering pipeline was overhauled.
 * SingleQuadParticle no longer supports custom vertex writing via render().
 * Instead, particles populate a QuadParticleRenderState via extract().
 * The custom directional billboard rendering with fading colors/alpha
 * needs to be reimplemented using the new system.
 * Currently using default SingleQuadParticle rendering as a placeholder.
 */
public class BulletHoleParticle extends SingleQuadParticle {
    private final Direction direction;
    private final BlockPos pos;
    private int uOffset;
    private int vOffset;
    private float textureDensity;

    public BulletHoleParticle(ClientLevel world, double x, double y, double z, Direction direction, BlockPos pos, String ammoId, String gunId, String gunDisplayId) {
        super(world, x, y, z, getBlockSprite(pos));
        this.direction = direction;
        this.pos = pos;
        this.lifetime = this.getLifetimeFromConfig(world);
        this.hasPhysics = false;
        this.gravity = 0.0F;
        this.quadSize = 0.05F;

        // Initialize UV offset from sprite
        this.uOffset = this.random.nextInt(16);
        this.vOffset = this.random.nextInt(16);
        if (this.sprite != null) {
            this.textureDensity = (this.sprite.getU1() - this.sprite.getU0()) / 16.0F;
        }

        BlockState state = world.getBlockState(pos);
        if (state.is(ModBlocks.TARGET) || shouldRemove()) {
            this.remove();
        }
        TimelessAPI.getGunDisplay(Identifier.parse(gunDisplayId), Identifier.parse(gunId)).ifPresent(gunIndex -> {
            float[] gunTracerColor = gunIndex.getTracerColor();
            if (gunTracerColor != null) {
                this.rCol = gunTracerColor[0];
                this.gCol = gunTracerColor[1];
                this.bCol = gunTracerColor[2];
            } else {
                TimelessAPI.getClientAmmoIndex(Identifier.parse(ammoId)).ifPresent(ammoIndex -> {
                    float[] ammoTracerColor = ammoIndex.getTracerColor();
                    this.rCol = ammoTracerColor[0];
                    this.gCol = ammoTracerColor[1];
                    this.bCol = ammoTracerColor[2];
                });
            }
        });
        this.alpha = 0.9F;
    }

    private static TextureAtlasSprite getBlockSprite(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        Level world = minecraft.level;
        if (world != null) {
            BlockState state = world.getBlockState(pos);
            return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(state);
        }
        return Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(TextureAtlas.LOCATION_BLOCKS).getSprite(MissingTextureAtlasSprite.getLocation());
    }

    private int getLifetimeFromConfig(ClientLevel world) {
        int configLife = RenderConfig.BULLET_HOLE_PARTICLE_LIFE.get();
        if (configLife <= 1) {
            return configLife;
        }
        return configLife + world.random.nextInt(configLife / 2);
    }

    @Override
    protected float getU0() {
        if (this.sprite == null || this.textureDensity == 0) return 0;
        return this.sprite.getU0() + this.uOffset * this.textureDensity;
    }

    @Override
    protected float getV0() {
        if (this.sprite == null || this.textureDensity == 0) return 0;
        return this.sprite.getV0() + this.vOffset * this.textureDensity;
    }

    @Override
    protected float getU1() {
        return this.getU0() + this.textureDensity;
    }

    @Override
    protected float getV1() {
        return this.getV0() + this.textureDensity;
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldRemove()) {
            this.remove();
        }
        // Update color/alpha fading over time
        if (this.lifetime > 0) {
            float colorPercent = Math.max(15 - this.age / 2, 0) / 15.0f;
            // Note: rCol/gCol/bCol are the base colors set in constructor
            // The fading is handled via alpha instead since we can't modify per-frame in extract()
            double threshold = RenderConfig.BULLET_HOLE_PARTICLE_FADE_THRESHOLD.get() * this.lifetime;
            float fade = 1.0f - (float) (Math.max(this.age - threshold, 0) / (this.lifetime - threshold));
            this.alpha = 0.9F * fade * colorPercent;
        }
    }

    @Override
    public void extract(QuadParticleRenderState renderState, Camera camera, float partialTicks) {
        // TODO: Custom directional billboard rendering lost in 1.21.11 migration.
        // The original render() method used Direction-based rotation for surface-aligned bullet holes.
        // The default extract() uses camera-facing quads which won't look correct for bullet holes.
        // This needs custom implementation using extractRotatedQuad with direction-based quaternion.
        super.extract(renderState, camera, partialTicks);
    }

    @Override
    protected Layer getLayer() {
        return Layer.TERRAIN;
    }

    private boolean shouldRemove() {
        final BlockState blockState = this.level.getBlockState(this.pos);
        if (blockState.isAir()) {
            return true;
        } else {
            // 阻止弹孔在与方块不构成有效附着时继续渲染
            VoxelShape shape = blockState.getCollisionShape(this.level, this.pos);
            if (shape.isEmpty()) {
                return true;
            }
            AABB baseBlockBoundingBox = shape.bounds();
            AABB blockBoundingBox = baseBlockBoundingBox.move(this.pos);
            boolean intersects = blockBoundingBox.intersects(
                    this.x - 0.1, this.y - 0.1, this.z - 0.1,
                    this.x + 0.1, this.y + 0.1, this.z + 0.1);
            return !intersects;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class Provider implements ParticleProvider<BulletHoleOption> {
        public Provider() {
        }

        @Override
        public BulletHoleParticle createParticle(@NotNull BulletHoleOption option, @NotNull ClientLevel world, double x, double y, double z, double pXSpeed, double pYSpeed, double pZSpeed, @NotNull RandomSource randomSource) {
            BulletHoleParticle particle = new BulletHoleParticle(world, x, y, z, option.getDirection(), option.getPos(), option.getAmmoId(), option.getGunId(), option.getGunDisplayId());
            return particle;
        }
    }
}
