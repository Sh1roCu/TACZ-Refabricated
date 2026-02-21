package com.tacz.guns.compat.playeranimator.animation;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import com.tacz.guns.GunMod;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class PlayerAnimatorAssetManager extends SimplePreparableReloadListener<Map<Identifier, HashMap<String, Animation>>> implements IdentifiableResourceReloadListener {
    private static PlayerAnimatorAssetManager INSTANCE;

    private final FileToIdConverter filetoidconverter = new FileToIdConverter("player_animator", ".json");
    private final HashMap<Identifier, HashMap<String, Animation>> animations = new HashMap<>();

    public static PlayerAnimatorAssetManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerAnimatorAssetManager();
        }
        return INSTANCE;
    }

    void putAnimation(Identifier id, InputStream stream) throws IOException {
        // Use UniversalAnimLoader to deserialize animations from JSON input stream.
        // This replaces the old AnimationCodecs.deserialize("json", () -> stream) API.
        Map<String, Animation> loadedAnimations = UniversalAnimLoader.loadAnimations(stream);
        for (var entry : loadedAnimations.entrySet()) {
            String name = entry.getKey().toLowerCase(Locale.ENGLISH);
            animations.computeIfAbsent(id, k -> Maps.newHashMap()).put(name, entry.getValue());
        }
    }

    Optional<Animation> getAnimations(Identifier id, String name) {
        var animationHashMap = this.animations.get(id);
        if (animationHashMap == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(animationHashMap.get(name));
    }

    public boolean containsKey(Identifier id) {
        return animations.containsKey(id);
    }

    public void clearAll() {
        animations.clear();
    }

    @Override
    protected Map<Identifier, HashMap<String, Animation>> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<Identifier, HashMap<String, Animation>> output = Maps.newHashMap();
        for (Map.Entry<Identifier, Resource> entry : filetoidconverter.listMatchingResources(manager).entrySet()) {
            Identifier resourcelocation = entry.getKey();
            Identifier resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try (InputStream stream = entry.getValue().open()) {
                // Use UniversalAnimLoader to deserialize animations from JSON input stream.
                Map<String, Animation> loadedAnimations = UniversalAnimLoader.loadAnimations(stream);
                for (var animEntry : loadedAnimations.entrySet()) {
                    String name = animEntry.getKey().toLowerCase(Locale.ENGLISH);
                    output.computeIfAbsent(resourcelocation1, k -> Maps.newHashMap()).put(name, animEntry.getValue());
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                GunMod.LOGGER.warn("Failed to player animation file: {}, entry: {}", resourcelocation, entry);
            }
        }
        return output;
    }

    @Override
    protected void apply(Map<Identifier, HashMap<String, Animation>> map, ResourceManager manager, ProfilerFiller profiler) {
        animations.clear();
        animations.putAll(map);
    }

    public static final Identifier ID = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "pa_asset_manager");

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
