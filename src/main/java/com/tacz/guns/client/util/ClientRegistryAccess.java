package com.tacz.guns.client.util;

import cn.sh1rocu.tacz.TaCZFabric;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;

public final class ClientRegistryAccess {
    private ClientRegistryAccess() {
    }

    public static HolderLookup.Provider registries() {
        var level = Minecraft.getInstance().level;
        return level == null ? TaCZFabric.VANILLA_ACCESS : level.registryAccess();
    }
}
