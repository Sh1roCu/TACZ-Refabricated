package com.tacz.guns.client.event;

import cn.sh1rocu.tacz.api.event.RenderTickEvent;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.tacz.guns.GunMod;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateContext;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.client.renderer.crosshair.CrosshairType;
import com.tacz.guns.config.client.RenderConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Environment(EnvType.CLIENT)
public class RenderCrosshairEvent {
    private static final Identifier HIT_ICON = Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "textures/crosshair/hit/hit_marker.png");
    private static final long KEEP_TIME = 300;
    private static boolean isRefitScreen = false;
    private static long hitTimestamp = -1L;
    private static long killTimestamp = -1L;
    private static long headShotTimestamp = -1L;

    /**
     * 当玩家手上拿着枪时，播放特定动画、或瞄准时需要隐藏准心
     */
    public static void onRenderOverlay(GuiGraphics guiGraphics, Window window, DeltaTracker deltaTracker, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!IGun.mainHandHoldGun(player)) {
            return;
        }

        // 全面替换成自己的
        ci.cancel();

        // 击中显示
        renderHitMarker(guiGraphics, window);
        // 换弹进行时取消准心渲染
        ReloadState reloadState = IGunOperator.fromLivingEntity(player).getSynReloadState();
        if (reloadState.getStateType().isReloading()) {
            return;
        }
        // 打开枪械改装界面的时候，取消准心渲染
        if (isRefitScreen) {
            return;
        }
        // 播放的动画需要隐藏准心时，取消准心渲染
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IGun)) {
            return;
        }

        IClientPlayerGunOperator playerGunOperator = IClientPlayerGunOperator.fromLocalPlayer(player);
        TimelessAPI.getGunDisplay(stack).ifPresent(gunIndex -> {
            // 瞄准快要完成时，取消准心渲染
            if (playerGunOperator.getClientAimingProgress(deltaTracker.getGameTimeDeltaPartialTick(false)) > 0.9) {
                // 枪包可以强制显示准星
                boolean forceShow = gunIndex.isShowCrosshair();
                // 越肩视角可以强制显示准星
                boolean shoulderSurfingForceShow = false /* TODO: ShoulderSurfing compat disabled */;
                // 两个强制都没有时，那么才允许隐藏
                if (!forceShow && !shoulderSurfingForceShow) {
                    return;
                }
            }

            AnimationStateMachine<?> animationStateMachine = gunIndex.getAnimationStateMachine();
            AnimationStateContext context = animationStateMachine.getContext();
            if (context == null || !context.shouldHideCrossHair()) {
                renderCrosshair(guiGraphics, window);
            }
        });
        GlStateManager._blendFuncSeparate(com.mojang.blaze3d.opengl.GlConst.GL_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE_MINUS_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE, com.mojang.blaze3d.opengl.GlConst.GL_ZERO);
    }

    public static void onRenderTick(RenderTickEvent event) {
        // 奇迹的是，RenderGameOverlayEvent.PreLayer 事件中，screen 还未被赋值...
        isRefitScreen = Minecraft.getInstance().screen instanceof GunRefitScreen;
    }

    private static void renderCrosshair(GuiGraphics graphics, Window window) {
        Options options = Minecraft.getInstance().options;
        // 越肩视角可以强制显示准星
        boolean shoulderSurfingForceShow = false /* TODO: ShoulderSurfing compat disabled */;
        if (!options.getCameraType().isFirstPerson() && !shoulderSurfingForceShow) {
            return;
        }
        if (options.hideGui) {
            return;
        }
        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        if (gameMode == null) {
            return;
        }
        if (gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();

        Identifier location = CrosshairType.getTextureLocation(RenderConfig.CROSSHAIR_TYPE.get());

        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(com.mojang.blaze3d.opengl.GlConst.GL_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE_MINUS_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE_MINUS_SRC_ALPHA);
        // TODO: In 1.21.11 RenderSystem.setShaderColor was removed; crosshair alpha=0.9 tinting not applied
        float x = width / 2f - 8;
        float y = height / 2f - 8;
        graphics.blit(location, (int) x, (int) y, 0, 0, 16, 16, 16, 16);
    }

    private static void renderHitMarker(GuiGraphics graphics, Window window) {
        long remainHitTime = System.currentTimeMillis() - hitTimestamp;
        long remainKillTime = System.currentTimeMillis() - killTimestamp;
        long remainHeadShotTime = System.currentTimeMillis() - headShotTimestamp;
        float offset = RenderConfig.HIT_MARKET_START_POSITION.get().floatValue();
        float fadeTime;

        if (remainKillTime > KEEP_TIME) {
            if (remainHitTime > KEEP_TIME) {
                return;
            } else {
                fadeTime = remainHitTime;
            }
        } else {
            // 最大位移为 4 像素
            offset += (remainKillTime * 4f) / KEEP_TIME;
            fadeTime = remainKillTime;
        }

        int width = window.getGuiScaledWidth();
        int height = window.getGuiScaledHeight();
        float x = width / 2f - 8;
        float y = height / 2f - 8;

        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(com.mojang.blaze3d.opengl.GlConst.GL_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE_MINUS_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_SRC_ALPHA, com.mojang.blaze3d.opengl.GlConst.GL_ONE_MINUS_SRC_ALPHA);
        // TODO: In 1.21.11 RenderSystem.setShaderColor was removed; hit marker fade/color tinting not applied

        graphics.blit(HIT_ICON, (int) (x - offset), (int) (y - offset), 0, 0, 8, 8, 16, 16);
        graphics.blit(HIT_ICON, (int) (x + 8 + offset), (int) (y - offset), 8, 0, 8, 8, 16, 16);
        graphics.blit(HIT_ICON, (int) (x - offset), (int) (y + 8 + offset), 0, 8, 8, 8, 16, 16);
        graphics.blit(HIT_ICON, (int) (x + 8 + offset), (int) (y + 8 + offset), 8, 8, 8, 8, 16, 16);
    }

    public static void markHitTimestamp() {
        RenderCrosshairEvent.hitTimestamp = System.currentTimeMillis();
    }

    public static void markKillTimestamp() {
        RenderCrosshairEvent.killTimestamp = System.currentTimeMillis();
    }

    public static void markHeadShotTimestamp() {
        RenderCrosshairEvent.headShotTimestamp = System.currentTimeMillis();
    }
}
