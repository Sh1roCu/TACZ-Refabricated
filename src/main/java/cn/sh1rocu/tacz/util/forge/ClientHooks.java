package cn.sh1rocu.tacz.util.forge;

import cn.sh1rocu.tacz.api.event.ClientPlayerNetworkEvent;
import cn.sh1rocu.tacz.api.event.ComputeFovModifierEvent;
import cn.sh1rocu.tacz.api.event.TextureStitchEvent;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.Connection;
import net.minecraft.world.entity.player.Player;

public class ClientHooks {
    public static float getFieldOfViewModifier(Player entity, float fovModifier) {
        ComputeFovModifierEvent fovModifierEvent = new ComputeFovModifierEvent(entity, fovModifier);
        ComputeFovModifierEvent.CALLBACK.invoker().post(fovModifierEvent);
        return fovModifierEvent.getNewFovModifier();
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        ClientPlayerNetworkEvent.CLONE.invoker().post(new ClientPlayerNetworkEvent.Clone(pc, oldPlayer, newPlayer, networkManager));
    }

    public static void onTextureStitchedPost(TextureAtlas map) {
        TextureStitchEvent.POST.invoker().post(new TextureStitchEvent.Post(map));
    }
}
