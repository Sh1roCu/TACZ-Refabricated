package com.tacz.guns.event;

import cn.sh1rocu.tacz.api.event.LivingKnockBackEvent;
import com.tacz.guns.api.entity.KnockBackModifier;

public class KnockbackChange {
    public static void onKnockback(LivingKnockBackEvent event) {
        KnockBackModifier modifier = KnockBackModifier.fromLivingEntity(event.getEntity());
        double strength = modifier.getKnockBackStrength();
        if (strength >= 0) {
            event.setStrength((float) strength);
        }
    }
}
