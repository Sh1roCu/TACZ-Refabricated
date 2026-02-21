package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.util.forge.network.IEntityExtension;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ServerEntity.class)
public class ServerEntityMixin {
    @Shadow
    @Final
    private Entity entity;

    @SuppressWarnings("unchecked")
    @Inject(method = "sendPairingData", at = @At(
            value = "INVOKE",
            target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
    ))
    private void sendComplexSpawnData(ServerPlayer serverPlayer, Consumer<Packet<ClientGamePacketListener>> consumer, CallbackInfo ci) {
        if (this.entity instanceof IEntityExtension entityExtension) {
            entityExtension.tacz$sendPairingData(serverPlayer, customPacketPayload -> ((Consumer<Packet<?>>) (Consumer<?>) consumer).accept(new ClientboundCustomPayloadPacket(customPacketPayload)));
        }
    }
}