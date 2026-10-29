package com.tacz.guns.init;

import cn.sh1rocu.tacz.api.event.AddPackFindersEvent;
import com.tacz.guns.GunMod;
import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.advancement.TargetHitTrigger;
import com.tacz.guns.entity.sync.ModSyncedEntityData;
import com.tacz.guns.resource.GunPackLoader;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class CommonRegistry {
    private static boolean LOAD_COMPLETE = false;

    public static void onSetupEvent() {
        AddPackFindersEvent.CALLBACK.register(CommonRegistry::onAddPackFinders);
        ModSyncedEntityData.init();
        registerCriteria();
        CauldronInteraction.WATER.map().put(ModItems.GUN_PEGBOARD, CauldronInteraction.DYED_ITEM);
        CauldronInteraction.WATER.map().put(ModItems.GLOW_GUN_PEGBOARD, CauldronInteraction.DYED_ITEM);
    }

    public static void registerCriteria() {
        register("target_hit", TargetHitTrigger.INSTANCE);
        register("gun_refit", GunRefitTrigger.INSTANCE);
    }

    private static void register(String name, CriterionTrigger<?> trigger) {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, name), trigger);
    }

    public static void onLoadComplete() {
        LOAD_COMPLETE = true;
    }

    public static boolean isLoadComplete() {
        return LOAD_COMPLETE;
    }

    public static void onAddPackFinders(AddPackFindersEvent event) {
        event.addRepositorySource(GunPackLoader.INSTANCE);
    }
}
