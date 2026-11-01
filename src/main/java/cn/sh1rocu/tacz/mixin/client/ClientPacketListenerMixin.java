package cn.sh1rocu.tacz.mixin.client;

import cn.sh1rocu.tacz.util.forge.ClientHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;addPlayer(ILnet/minecraft/client/player/AbstractClientPlayer;)V"))
    private void tacz$cloneEvent(ClientboundRespawnPacket packet, CallbackInfo ci, @Local(ordinal = 0) LocalPlayer oldPlayer, @Local(ordinal = 1) LocalPlayer newPlayer) {
        ClientHooks.firePlayerRespawn(this.minecraft.gameMode, oldPlayer, newPlayer, newPlayer.connection.getConnection());
    }
}
