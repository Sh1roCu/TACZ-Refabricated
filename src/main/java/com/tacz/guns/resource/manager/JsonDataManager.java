package com.tacz.guns.resource.manager;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tacz.guns.GunMod;
import com.tacz.guns.util.ResourceScanner;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * 通用数据管理器<br>
 * 从资源包/数据包中读取json文件并解析为数据
 *
 * @param <T> 数据类型
 */
public class JsonDataManager<T> extends SimplePreparableReloadListener<Map<Identifier, JsonElement>> implements IdentifiableResourceReloadListener {
    protected final Map<Identifier, T> dataMap = Maps.newHashMap();

    private final Gson gson;
    private final Class<T> dataClass;
    private final Marker marker;

    private final FileToIdConverter fileToIdConverter;

    public final Identifier ID;

    public JsonDataManager(Class<T> dataClass, Gson pGson, String directory, String marker) {
        this(dataClass, pGson, FileToIdConverter.json(directory), marker);
    }

    public JsonDataManager(Class<T> dataClass, Gson pGson, FileToIdConverter fileToIdConverter, String marker) {
        this.gson = pGson;
        this.dataClass = dataClass;
        this.marker = MarkerFactory.getMarker(marker);
        this.ID = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, marker.toLowerCase(Locale.ROOT));
        this.fileToIdConverter = fileToIdConverter;
    }

    @NotNull
    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return ResourceScanner.scanDirectory(pResourceManager, fileToIdConverter, this.gson);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        dataMap.clear();
        for (Map.Entry<Identifier, JsonElement> entry : pObject.entrySet()) {
            Identifier id = entry.getKey();
            JsonElement element = entry.getValue();
            try {
                T data = parseJson(element);
                if (data != null) {
                    dataMap.put(id, data);
                }
            } catch (JsonParseException | IllegalArgumentException e) {
                GunMod.LOGGER.error(marker, "Failed to load data file {}", id, e);
            }
        }
    }

    protected T parseJson(JsonElement element) {
        return gson.fromJson(element, getDataClass());
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public Marker getMarker() {
        return marker;
    }

    public Gson getGson() {
        return gson;
    }

    public T getData(Identifier id) {
        return dataMap.get(id);
    }

    public Map<Identifier, T> getAllData() {
        return dataMap;
    }

    @Override
    public Identifier getFabricId() {
        return this.ID;
    }
}
