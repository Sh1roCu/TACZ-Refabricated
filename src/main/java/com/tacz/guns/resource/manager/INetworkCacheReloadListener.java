package com.tacz.guns.resource.manager;

import com.tacz.guns.resource.network.DataType;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface INetworkCacheReloadListener extends IdentifiableResourceReloadListener {
    Map<ResourceLocation, String> getNetworkCache();

    DataType getType();
}
