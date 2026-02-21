package com.tacz.guns.compat.playeranimator;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.compat.playeranimator.animation.AnimationDataRegisterFactory;
import com.tacz.guns.compat.playeranimator.animation.AnimationManager;
import com.tacz.guns.compat.playeranimator.animation.PlayerAnimatorAssetManager;
import com.tacz.guns.compat.playeranimator.animation.PlayerAnimatorLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import java.io.File;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

public class PlayerAnimatorCompat {
    public static Identifier LOWER_ANIMATION = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "lower_animation");
    public static Identifier LOOP_UPPER_ANIMATION = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "loop_upper_animation");
    public static Identifier ONCE_UPPER_ANIMATION = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "once_upper_animation");
    public static Identifier ROTATION_ANIMATION = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "rotation");

    private static final String PA = "playeranimator";
    private static boolean INSTALLED = false;

    public static void init() {
        INSTALLED = FabricLoader.getInstance().isModLoaded(PA);
        if (isInstalled()) {
            AnimationDataRegisterFactory.registerData();
            AnimationManager manager = new AnimationManager();
            GunShootEvent.CALLBACK.register(manager::onFire);
            GunReloadEvent.CALLBACK.register(manager::onReload);
            GunMeleeEvent.CALLBACK.register(manager::onMelee);
            GunDrawEvent.CALLBACK.register(manager::onDraw);
        }
    }

    public static boolean loadAnimationFromZip(ZipFile zipFile, String zipPath) {
        if (isInstalled()) {
            return PlayerAnimatorLoader.load(zipFile, zipPath);
        }
        return false;
    }

    public static void loadAnimationFromFile(File file) {
        if (isInstalled()) {
            PlayerAnimatorLoader.load(file);
        }
    }

    public static void clearAllAnimationCache() {
        if (isInstalled()) {
            PlayerAnimatorAssetManager.get().clearAll();
        }
    }

    public static boolean hasPlayerAnimator3rd(LivingEntity livingEntity, GunDisplayInstance display) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayer) {
            return AnimationManager.hasPlayerAnimator3rd(display);
        }
        return false;
    }

    public static void stopAllAnimation(LivingEntity livingEntity) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayer player) {
            AnimationManager.stopAllAnimation(player);
        }
    }

    public static void stopAllAnimation(LivingEntity livingEntity, int fadeTime) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayer player) {
            AnimationManager.stopAllAnimation(player, fadeTime);
        }
    }

    public static void playAnimation(LivingEntity livingEntity, GunDisplayInstance display, float limbSwingAmount) {
        if (isInstalled() && livingEntity instanceof AbstractClientPlayer player) {
            AnimationManager.playLowerAnimation(player, display, limbSwingAmount);
            AnimationManager.playLoopUpperAnimation(player, display, limbSwingAmount);
            AnimationManager.playRotationAnimation(player, display);
        }
    }

    public static boolean isInstalled() {
        return INSTALLED;
    }

    public static void registerReloadListener(Consumer<IdentifiableResourceReloadListener> register) {
        if (isInstalled()) {
            register.accept(PlayerAnimatorAssetManager.get());
        }
    }
}
