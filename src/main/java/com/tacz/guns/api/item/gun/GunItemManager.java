package com.tacz.guns.api.item.gun;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

public class GunItemManager {
    private static final Map<String, AbstractGunItem> GUN_ITEM_MAP = Maps.newHashMap();

    public static void registerGunItem(String name, AbstractGunItem registryObject) {
        GUN_ITEM_MAP.put(name, registryObject);
    }

    public static AbstractGunItem getGunItemRegistryObject(String key) {
        return GUN_ITEM_MAP.get(key);
    }

    public static Collection<AbstractGunItem> getAllGunItems() {
        return GUN_ITEM_MAP.values();
    }
}
