package cn.sh1rocu.tacz.util.neoforge;

import cn.sh1rocu.tacz.api.event.AddReloadListenerEvent;
import cn.sh1rocu.tacz.api.event.PlayerEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class EventHooks {
    public static void firePlayerLoggedIn(Player player) {
        PlayerEvent.LOGGED_IN.invoker().post(new PlayerEvent.PlayerLoggedInEvent(player));
    }

    public static void firePlayerLoggedOut(Player player) {
        PlayerEvent.LOGGED_OUT.invoker().post(new PlayerEvent.PlayerLoggedOutEvent(player));
    }

    public static List<PreparableReloadListener> onResourceReload(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
        AddReloadListenerEvent event = new AddReloadListenerEvent(serverResources, registryAccess);
        AddReloadListenerEvent.CALLBACK.invoker().post(event);
        return event.getListeners();
    }
}
