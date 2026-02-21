package com.tacz.guns.resource;

import com.google.common.collect.Maps;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.tacz.guns.GunMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class VersionChecker {
    private static final Marker MARKER = MarkerFactory.getMarker("VersionChecker");
    private static final Pattern PACK_INFO_PATTERN = Pattern.compile("^\\w+/pack\\.json$");
    private static final Map<Path, Boolean> VERSION_CHECK_CACHE = Maps.newHashMap();

    public static boolean match(File dir) {
        return VERSION_CHECK_CACHE.computeIfAbsent(dir.toPath(), path -> checkDirVersion(dir));
    }

    public static boolean noneMatch(ZipFile zipFile, Path zipFilePath) {
        return !VERSION_CHECK_CACHE.computeIfAbsent(zipFilePath, path -> checkZipVersion(zipFile));
    }

    public static void clearCache() {
        VERSION_CHECK_CACHE.clear();
    }

    private static boolean checkDirVersion(File root) {
        if (!root.isDirectory()) {
            return false;
        }
        Path packInfoFilePath = root.toPath().resolve("pack.json");
        // 如果文件不存在，说明不检查版本信息，返回 true
        if (Files.notExists(packInfoFilePath)) {
            return true;
        }
        try (InputStream stream = Files.newInputStream(packInfoFilePath)) {
            com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            jsonReader.setStrictness(com.google.gson.Strictness.LENIENT);
            Info info = CommonAssetsManager.GSON.fromJson(jsonReader, Info.class);
            return modVersionAllMatch(info);
        } catch (IOException | JsonSyntaxException | JsonIOException | VersionParsingException exception) {
            GunMod.LOGGER.warn(MARKER, "Failed to read info json: {}", packInfoFilePath);
            GunMod.LOGGER.warn(exception.getMessage());
        }
        return true;
    }

    private static boolean checkZipVersion(ZipFile zipFile) {
        Enumeration<? extends ZipEntry> iteration = zipFile.entries();
        while (iteration.hasMoreElements()) {
            String path = iteration.nextElement().getName();
            Matcher matcher = PACK_INFO_PATTERN.matcher(path);
            if (!matcher.matches()) {
                continue;
            }
            ZipEntry entry = zipFile.getEntry(path);
            // 如果文件不存在，说明不检查版本信息，返回 true
            if (entry == null) {
                return true;
            }
            try (InputStream stream = zipFile.getInputStream(entry)) {
                com.google.gson.stream.JsonReader jsonReader = new com.google.gson.stream.JsonReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
                jsonReader.setStrictness(com.google.gson.Strictness.LENIENT);
                Info info = CommonAssetsManager.GSON.fromJson(jsonReader, Info.class);
                // 只要有一个不符，那么就不加载
                if (!modVersionAllMatch(info)) {
                    return false;
                }
            } catch (IOException | JsonSyntaxException | JsonIOException |
                     VersionParsingException exception) {
                GunMod.LOGGER.warn(MARKER, "Failed to read info json: {}", path);
                GunMod.LOGGER.warn(exception.getMessage());
            }
        }
        return true;
    }

    private static boolean modVersionAllMatch(Info info) throws VersionParsingException {
        HashMap<String, String> dependencies = info.getDependencies();
        for (String modId : dependencies.keySet()) {
            if (!modVersionMatch(modId, dependencies.get(modId))) {
                return false;
            }
        }
        return true;
    }

    private static boolean modVersionMatch(String modId, String version) throws VersionParsingException {
        VersionPredicate versionRange = VersionPredicate.parse(version);
        return FabricLoader.getInstance().getModContainer(modId).map(mod -> {
            Version modVersion = mod.getMetadata().getVersion();
            return versionRange.test(modVersion);
        }).orElse(false);
    }

    private static class Info {
        @SerializedName("dependencies")
        private HashMap<String, String> dependencies = Maps.newHashMap();

        public HashMap<String, String> getDependencies() {
            return dependencies;
        }
    }
}
