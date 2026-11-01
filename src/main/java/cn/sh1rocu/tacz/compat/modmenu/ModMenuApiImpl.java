package cn.sh1rocu.tacz.compat.modmenu;

import com.tacz.guns.compat.cloth.MenuIntegration;
import com.tacz.guns.init.CompatRegistry;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded(CompatRegistry.CLOTH_CONFIG))
            return parent -> MenuIntegration.getConfigBuilder().setParentScreen(parent).build();
        return null;
    }
}