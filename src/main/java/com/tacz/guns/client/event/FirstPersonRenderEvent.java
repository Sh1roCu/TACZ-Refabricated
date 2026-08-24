package com.tacz.guns.client.event;

import cn.sh1rocu.simplebedrockmodel.api.event.RenderHandEvent;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.client.other.KeepingItemRenderer;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.compat.RecordingCompatHelper;
import com.tacz.guns.client.renderer.item.AnimateGeoItemRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class FirstPersonRenderEvent {
    private static AnimationStateMachine<?> lastStateMachine = null;

    public static void onRenderHand(RenderHandEvent event) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }
        if (event.getHand() == InteractionHand.OFF_HAND) {
            ItemStack stack = KeepingItemRenderer.getRenderer().getCurrentItem();
            if (stack.getItem() instanceof IGun) {
                event.setCanceled(true);
            }
            return;
        }
        // 事件事件给的是被延长渲染修改过后的物品，不是玩家实际手持的
        ItemStack stack = event.getItemStack();

        // Use the view player from recording compat (spectating player in replay)
        Player viewPlayer = RecordingCompatHelper.getViewPlayer();

        // During replay, the event item stack comes from the local player (who holds nothing).
        // Use the spectating player's held item instead.
        if (viewPlayer != null) {
            stack = viewPlayer.getMainHandItem();
        }

        // 获取 TransformType
        ItemDisplayContext transformType;
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            transformType = ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        } else {
            transformType = ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
        }

        // 渲染相关内容整理到物品的IClientItemExtensions了，这个接口有待进一步抽象
        if (BuiltinItemRendererRegistry.INSTANCE.get(stack.getItem()) instanceof AnimateGeoItemRenderer<?, ?> renderer) {
            // 如果旧的状态机已经不再使用且未正常退出，使其静默退出
            AnimationStateMachine<?> machine = renderer.getStateMachine(stack);
            if (machine != lastStateMachine) {
                if (lastStateMachine != null && lastStateMachine.isInitialized()) {
                    lastStateMachine.exit();
                }
                lastStateMachine = machine;
            }

            // 物品处于后台时，阻止状态机初始化
            boolean flag = ItemStack.matches(viewPlayer != null ? viewPlayer.getMainHandItem() : localPlayer.getMainHandItem(), stack);
            if (flag && renderer.needReInit(stack)) {
                renderer.tryInit(stack, viewPlayer != null ? viewPlayer : localPlayer, event.getPartialTick());
            }

            renderer.renderFirstPerson(localPlayer, stack, transformType, event.getPoseStack(), event.getMultiBufferSource(),
                    event.getPackedLight(), event.getPartialTick());
            event.setCanceled(true);
        }
    }
}
