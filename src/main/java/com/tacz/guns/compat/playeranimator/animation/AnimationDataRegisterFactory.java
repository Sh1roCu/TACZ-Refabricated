package com.tacz.guns.compat.playeranimator.animation;

import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.zigythebird.playeranimcore.animation.layered.ModifierLayer;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;

public class AnimationDataRegisterFactory {
    public static void registerData() {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PlayerAnimatorCompat.LOWER_ANIMATION, 93, avatar -> new ModifierLayer<>());
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, 94, avatar -> new ModifierLayer<>());
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, 95, avatar -> new ModifierLayer<>());
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(PlayerAnimatorCompat.ROTATION_ANIMATION, 96,
                avatar -> {
                    // Avatar extends from LivingEntity -> we need Player for the modifier
                    if (avatar instanceof net.minecraft.world.entity.player.Player player) {
                        return new ModifierLayer<>(null, AdjustmentYRotModifier.getModifier(player));
                    }
                    return new ModifierLayer<>();
                });
    }
}
