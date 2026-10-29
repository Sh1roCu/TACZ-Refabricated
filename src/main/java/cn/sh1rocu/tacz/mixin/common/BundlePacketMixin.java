package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.util.neoforge.network.IEntityWithComplexSpawn;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * this is needed for {@link IEntityWithComplexSpawn} to work properly on dedicated servers, since it ends up nesting bundle packets.
 */
@Mixin(BundlePacket.class)
public class BundlePacketMixin {
    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
    private static Iterable<Packet<?>> flattenPackets(Iterable<Packet<?>> packets) {
        List<Packet<?>> list = new ArrayList<>();
        recursivelyCollectBundledPackets(packets, list);
        return list;
    }

    @Unique
    private static void recursivelyCollectBundledPackets(Iterable<Packet<?>> packets, List<Packet<?>> list) {
        for (Packet<?> packet : packets) {
            if (packet instanceof BundlePacket<?> bundle) {
                //noinspection unchecked,rawtypes
                recursivelyCollectBundledPackets((Iterable) bundle.subPackets(), list);
            } else {
                list.add(packet);
            }
        }
    }
}