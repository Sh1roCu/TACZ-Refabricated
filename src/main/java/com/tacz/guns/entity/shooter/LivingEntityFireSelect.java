package com.tacz.guns.entity.shooter;

import cn.sh1rocu.tacz.api.LogicalSide;
import com.tacz.guns.api.event.common.GunFireSelectEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.network.message.event.ServerMessageGunFireSelect;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class LivingEntityFireSelect {
    private final LivingEntity shooter;
    private final ShooterDataHolder data;

    public LivingEntityFireSelect(LivingEntity shooter, ShooterDataHolder data) {
        this.shooter = shooter;
        this.data = data;
    }

    public void fireSelect() {
        if (data.currentGunItem == null) {
            return;
        }
        ItemStack currentGunItem = data.currentGunItem.get();
        if (!(currentGunItem.getItem() instanceof IGun iGun)) {
            return;
        }
        GunFireSelectEvent fireSelectEvent = new GunFireSelectEvent(shooter, currentGunItem, LogicalSide.SERVER);
        GunFireSelectEvent.CALLBACK.invoker().post(fireSelectEvent);
        if (fireSelectEvent.isCanceled()) {
            return;
        }
        NetworkHandler.sendToTrackingEntityAndSelf(shooter, new ServerMessageGunFireSelect(shooter.getId(), currentGunItem));
        if (iGun instanceof AbstractGunItem logicGun) {
            logicGun.fireSelect(data, currentGunItem);
            // 刷新配件缓存
            AttachmentPropertyManager.postChangeEvent(shooter, currentGunItem);
        }
    }
}
