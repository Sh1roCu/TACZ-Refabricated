package com.tacz.guns.compat.kubejs.custom;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.compat.kubejs.TimelessKubeJSPlugin;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class CustomGunItemBuilder extends ItemBuilder {
    public String typeName;

    public CustomGunItemBuilder(ResourceLocation i) {
        super(i);
        this.typeName = "kubejs_default";
    }

    public void setTypeName(String name) {
        this.typeName = name;
    }

    @Override
    public Item createObject() {
        TimelessKubeJSPlugin.registerGunType(typeName, (AbstractGunItem) BuiltInRegistries.ITEM.get(this.id));
        return new KubeJSCustomGunItem();
    }
}
