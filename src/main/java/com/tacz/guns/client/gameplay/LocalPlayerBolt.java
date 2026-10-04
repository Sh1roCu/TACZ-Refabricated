package com.tacz.guns.client.gameplay;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.client.animation.statemachine.AnimationStateMachine;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.animation.statemachine.GunAnimationConstant;
import com.tacz.guns.client.resource.index.ClientGunIndex;
import com.tacz.guns.client.sound.SoundPlayManager;
import com.tacz.guns.network.message.ClientMessagePlayerBoltGun;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

public class LocalPlayerBolt {
    private final LocalPlayerDataHolder data;
    private final LocalPlayer player;

    public LocalPlayerBolt(LocalPlayerDataHolder data, LocalPlayer player) {
        this.data = data;
        this.player = player;
    }

    public void bolt() {
        // 检查状态锁
        if (data.clientStateLock) {
            return;
        }
        if (data.isBolting) {
            return;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof IGun iGun)) {
            return;
        }
        GunData gunData = TimelessAPI.getClientGunIndex(iGun.getGunId(mainHandItem)).map(ClientGunIndex::getGunData).orElse(null);
        if (gunData == null) {
            return;
        }

        TimelessAPI.getGunDisplay(mainHandItem).ifPresent(display -> {
            IGunOperator gunOperator = IGunOperator.fromLivingEntity(player);
            // 检查 bolt 类型是否是 manual action
            Bolt boltType = gunData.getBolt();
            // 是否为背包直读
            boolean useInventoryAmmo = iGun.useInventoryAmmo(mainHandItem);
            // 膛内是否有子弹
            boolean hasAmmoInBarrel = iGun.hasBulletInBarrel(mainHandItem) && boltType != Bolt.OPEN_BOLT;
            // 背包内是否还有子弹 (创造模式是否消耗背包备弹)
            boolean hasInventoryAmmo = iGun.hasInventoryAmmo(player, mainHandItem, gunOperator.needCheckAmmo());
            // 判断没有子弹的条件 (背包直读且包内没子弹 / 非背包直读且弹匣子弹数 < 1)
            boolean noAmmo = useInventoryAmmo && !hasInventoryAmmo ||
                    !useInventoryAmmo && iGun.getCurrentAmmoCount(mainHandItem) < 1;
            if (boltType != Bolt.MANUAL_ACTION) {
                return;
            }
            // 检查是否有弹药在枪膛内
            if (hasAmmoInBarrel) {
                return;
            }
            // 检查弹匣内是否有子弹
            if (noAmmo) {
                return;
            }
            // 锁上状态锁
            data.lockState(IGunOperator::getSynIsBolting);
            data.isBolting = true;
            // 发包通知服务器
            ClientPlayNetworking.send(new ClientMessagePlayerBoltGun());
            // 播放动画和音效
            AnimationStateMachine<?> animationStateMachine = display.getAnimationStateMachine();
            if (animationStateMachine != null) {
                SoundPlayManager.playBoltSound(player, display);
                animationStateMachine.trigger(GunAnimationConstant.INPUT_BOLT);
            }
        });
    }

    public void tickAutoBolt() {
        ItemStack mainHandItem = player.getMainHandItem();
        if (!(mainHandItem.getItem() instanceof IGun iGun)) {
            data.isBolting = false;
            return;
        }
        bolt();
        if (data.isBolting) {
            // 使用服务端同步的 isBolting 实体数据来判断拉栓是否完成，
            // 而不是依赖物品 NBT 同步（hasBulletInBarrel），因为 NBT 同步有网络延迟，
            // 在多人游戏中会导致客户端认为拉栓未完成而卡死。
            if (!IGunOperator.fromLivingEntity(player).getSynIsBolting()) {
                data.isBolting = false;
            }
        }
    }
}
