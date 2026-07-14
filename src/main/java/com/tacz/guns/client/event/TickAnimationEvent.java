package com.tacz.guns.client.event;

import cn.sh1rocu.simplebedrockmodel.api.event.RenderTickEvent;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.animation.statemachine.GunAnimationConstant;
import com.tacz.guns.client.renderer.item.AnimateGeoItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class TickAnimationEvent {
    public static void tickAnimation(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        TimelessAPI.getGunDisplay(mainHandItem).ifPresent(gunIndex -> {
            var animationStateMachine = gunIndex.getAnimationStateMachine();
            if (animationStateMachine == null) {
                return;
            }
            // 群组服切世界导致的特殊 BUG 处理，正常情况不会遇到此问题
            if (player.input == null) {
                animationStateMachine.trigger(GunAnimationConstant.INPUT_IDLE);
                return;
            }
            if (!player.isMovingSlowly() && player.isSprinting()) {
                // 如果玩家正在移动，播放移动动画，否则播放 idle 动画
                animationStateMachine.trigger(GunAnimationConstant.INPUT_RUN);
            } else if (!player.isMovingSlowly() && player.input.getMoveVector().length() > 0.01) {
                animationStateMachine.trigger(GunAnimationConstant.INPUT_WALK);
            } else {
                animationStateMachine.trigger(GunAnimationConstant.INPUT_IDLE);
            }
        });
    }

    public static void tickAnimation(RenderTickEvent event) {
        if (event.phase == RenderTickEvent.Phase.END) {
            return;
        }
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (BuiltinItemRendererRegistry.INSTANCE.get(mainHandItem.getItem()) instanceof AnimateGeoItemRenderer<?, ?> renderer) {
            // 如果物品不一样了，先尝试初始化状态机
            if (renderer.needReInit(mainHandItem)) {
                renderer.tryInit(mainHandItem, player, event.getTimer().getGameTimeDeltaPartialTick(false));
            }
            renderer.visualUpdate(mainHandItem);
        }
    }
}
