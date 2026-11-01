package com.tacz.guns.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class PreLoadModConfig extends ModConfig {
    private CommentedConfig configData;
    private final String modId;

    public PreLoadModConfig(Type type, IConfigSpec<?> spec, String modId, String fileName) {
        super(type, spec, modId, fileName);
        this.modId = modId;
    }

    public CommentedConfig getConfigData() {
        return this.configData;
    }

    public void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.getSpec().acceptConfig(this.configData);
    }

    public void fireEvent(final ModConfig config) {
        ModConfigEvents.loading(modId).invoker().onModConfigLoading(config);
    }

    public void save() {
        ((CommentedFileConfig) this.configData).save();
    }

    public Path getFullPath() {
        return ((CommentedFileConfig) this.configData).getNioPath();
    }
}
