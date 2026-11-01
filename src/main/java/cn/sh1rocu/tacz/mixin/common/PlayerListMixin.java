package cn.sh1rocu.tacz.mixin.common;

import cn.sh1rocu.tacz.util.forge.EventHooks;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void $tacz$placeNewPlayer(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        EventHooks.firePlayerLoggedIn(player);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private void tacz$remove(ServerPlayer player, CallbackInfo ci) {
        EventHooks.firePlayerLoggedOut(player);
    }
}
