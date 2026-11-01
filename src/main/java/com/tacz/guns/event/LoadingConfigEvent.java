package com.tacz.guns.event;

import com.tacz.guns.config.util.HeadShotAABBConfigRead;
import com.tacz.guns.config.util.InteractKeyConfigRead;
import net.minecraftforge.fml.config.ModConfig;

public class LoadingConfigEvent {
    private static final String CONFIG_NAME = "tacz-server.toml";

    /**
     * 客户端和服务端启动时，会触发此事件
     */
    public static void onLoadingConfig(ModConfig config) {
        String fileName = config.getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            HeadShotAABBConfigRead.init();
            InteractKeyConfigRead.init();
        }
    }

    /**
     * 玩家进入服务端，或者服务端自动重置配置时，会触发此方法
     */
    public static void onReloadingConfig(ModConfig config) {
        String fileName = config.getFileName();
        if (CONFIG_NAME.equals(fileName)) {
            HeadShotAABBConfigRead.init();
            InteractKeyConfigRead.init();
//            if (FabricLoader.getInstance().getEnvironmentType()== EnvType.CLIENT) ClientGunPackDownloadManager::downloadClientGunPack;
        }
    }
}
