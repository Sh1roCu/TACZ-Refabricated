package com.tacz.guns.util;

import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.util.block.ProjectileExplosion;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ExplodeUtil {
    public static void createExplosion(Entity owner, Entity exploder, float damage, float radius, boolean knockback, boolean destroy, Vec3 hitPos) {
        // Client does not execute
        if (!(exploder.level() instanceof ServerLevel level)) {
            return;
        }
        // Read block destruction mode from config
        Explosion.BlockInteraction mode;
        if (destroy) {
            mode = Explosion.BlockInteraction.DESTROY;
        } else {
            mode = Explosion.BlockInteraction.KEEP;
        }
        // Create explosion
        ProjectileExplosion explosion = new ProjectileExplosion(level, owner, exploder, null, null, hitPos.x(), hitPos.y(), hitPos.z(), damage, radius, knockback, mode);
        // Execute explosion logic
        explosion.explode();
        if (mode == Explosion.BlockInteraction.KEEP) {
            explosion.clearToBlow();
        }
        // Send explosion packet to nearby clients
        level.players().stream().filter(player -> Mth.sqrt((float) player.distanceToSqr(hitPos)) < AmmoConfig.EXPLOSIVE_AMMO_VISIBLE_DISTANCE.get()).forEach(player -> {
            Vec3 playerKnockback = explosion.getHitPlayers().get(player);
            ClientboundExplodePacket packet = new ClientboundExplodePacket(
                    new Vec3(hitPos.x(), hitPos.y(), hitPos.z()),
                    radius,
                    explosion.getToBlow().size(),
                    Optional.ofNullable(playerKnockback),
                    ParticleTypes.EXPLOSION,
                    SoundEvents.GENERIC_EXPLODE,
                    WeightedList.of()
            );
            player.connection.send(packet);
        });
    }
}
