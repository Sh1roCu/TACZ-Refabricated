package com.tacz.guns.client.resource.manager;

import com.google.common.collect.Maps;
import com.tacz.guns.GunMod;
import com.tacz.guns.client.resource.manager.SoundAssetsManager.SoundData;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public class SoundAssetsManager extends SimplePreparableReloadListener<Map<Identifier, SoundData>> implements IdentifiableResourceReloadListener {
    public record SoundData(ByteBuffer byteBuffer, AudioFormat audioFormat) {
    }

    private static final Marker MARKER = MarkerFactory.getMarker("SoundsLoader");

    private final Map<Identifier, SoundData> dataMap = Maps.newHashMap();
    private final FileToIdConverter filetoidconverter = new FileToIdConverter("tacz_sounds", ".ogg");

    @Override
    @NotNull
    protected Map<Identifier, SoundData> prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        Map<Identifier, SoundData> output = Maps.newHashMap();
        for (Map.Entry<Identifier, Resource> entry : filetoidconverter.listMatchingResources(pResourceManager).entrySet()) {
            Identifier resourcelocation = entry.getKey();
            Identifier resourcelocation1 = filetoidconverter.fileToId(resourcelocation);

            try (InputStream stream = entry.getValue().open(); JOrbisAudioStream audioStream = new JOrbisAudioStream(stream)) {
                ByteBuffer bytebuffer = audioStream.readAll();
                output.put(resourcelocation1, new SoundData(bytebuffer, audioStream.getFormat()));
            } catch (IOException exception) {
                GunMod.LOGGER.warn(MARKER, "Failed to read sound file: {}", resourcelocation);
                exception.printStackTrace();
            }
        }
        return output;
    }

    @Override
    protected void apply(Map<Identifier, SoundData> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        dataMap.clear();
        dataMap.putAll(pObject);
    }

    public SoundData getData(Identifier id) {
        return dataMap.get(id);
    }

    public static final Identifier ID = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "sound_assets_manager");

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
