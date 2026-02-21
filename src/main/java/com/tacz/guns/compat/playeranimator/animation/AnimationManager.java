package com.tacz.guns.compat.playeranimator.animation;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunDrawEvent;
import com.tacz.guns.api.event.common.GunMeleeEvent;
import com.tacz.guns.api.event.common.GunReloadEvent;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.compat.playeranimator.AnimationName;
import com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.animation.layered.ModifierLayer;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

public class AnimationManager {
    public static boolean hasPlayerAnimator3rd(GunDisplayInstance display) {
        Identifier location = display.getPlayerAnimator3rd();
        if (location == null) {
            return false;
        }
        return PlayerAnimatorAssetManager.get().containsKey(location);
    }

    public static boolean isFlying(AbstractClientPlayer player) {
        return !player.onGround() && player.getAbilities().flying;
    }

    public static void playRotationAnimation(AbstractClientPlayer player, GunDisplayInstance display) {
        String animationName = AnimationName.EMPTY;
        Identifier dataId = PlayerAnimatorCompat.ROTATION_ANIMATION;
        Identifier animator3rd = display.getPlayerAnimator3rd();
        if (animator3rd == null) {
            return;
        }
        if (!PlayerAnimatorAssetManager.get().containsKey(animator3rd)) {
            return;
        }
        PlayerAnimatorAssetManager.get().getAnimations(animator3rd, animationName).ifPresent(animation -> {
            var animLayer = PlayerAnimationAccess.getPlayerAnimationLayer(player, dataId);
            if (!(animLayer instanceof ModifierLayer<?> layer)) {
                return;
            }
            @SuppressWarnings("unchecked")
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) layer;
            AbstractFadeModifier fadeModifier = AbstractFadeModifier.standardFadeIn(8, EasingType.EASE_IN_OUT_SINE);
            PlayerAnimationController controller = createController(player, animation);
            modifierLayer.replaceAnimationWithFade(fadeModifier, controller);
        });
    }

    public static void playLowerAnimation(AbstractClientPlayer player, GunDisplayInstance display, float limbSwingAmount) {
        // If player is lying down, don't play lower body animation
        if (isPlayerLie(player)) {
            return;
        }
        // If player is riding
        if (player.getVehicle() != null) {
            playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.RIDE_LOWER);
            return;
        }
        // If player is flying, lower body animation is standing
        if (isFlying(player)) {
            playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.HOLD_LOWER);
            return;
        }
        if (player.isSprinting()) {
            if (player.getPose() == Pose.CROUCHING) {
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.CROUCH_WALK_LOWER);
            } else {
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.RUN_LOWER);
            }
            return;
        }
        if (limbSwingAmount > 0.05) {
            if (player.getPose() == Pose.CROUCHING) {
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.CROUCH_WALK_LOWER);
            } else {
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.WALK_LOWER);
            }
            return;
        }
        if (player.getPose() == Pose.CROUCHING) {
            playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.CROUCH_LOWER);
        } else {
            playLoopAnimation(player, display, PlayerAnimatorCompat.LOWER_ANIMATION, AnimationName.HOLD_LOWER);
        }
    }

    public static void playLoopUpperAnimation(AbstractClientPlayer player, GunDisplayInstance display, float limbSwingAmount) {
        IGunOperator operator = IGunOperator.fromLivingEntity(player);
        float aimingProgress = operator.getSynAimingProgress();
        if (aimingProgress <= 0) {
            // Animation when sprinting
            if (!isFlying(player) && player.isSprinting()) {
                if (isPlayerLie(player)) {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.LIE_MOVE);
                } else if (player.getPose() == Pose.CROUCHING) {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.CROUCH_WALK_UPPER);
                } else {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.RUN_UPPER);
                }
                return;
            }

            // Animation when walking
            if (!isFlying(player) && limbSwingAmount > 0.05) {
                if (isPlayerLie(player)) {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.LIE_MOVE);
                } else if (player.getPose() == Pose.CROUCHING) {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.CROUCH_WALK_UPPER);
                } else {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.WALK_UPPER);
                }
                return;
            }

            if (isPlayerLie(player)) {
                // Animation when lying down
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.LIE);
            } else {
                // Normal idle
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.HOLD_UPPER);
            }
        } else {
            if (isPlayerLie(player)) {
                // Aiming while lying down
                if (!isFlying(player) && limbSwingAmount > 0.05) {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.LIE_MOVE);
                } else {
                    playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.LIE_AIM);
                }
            } else {
                // Normal aiming
                playLoopAnimation(player, display, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, AnimationName.AIM_UPPER);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void playLoopAnimation(AbstractClientPlayer player, GunDisplayInstance display, Identifier dataId, String animationName) {
        Identifier animator3rd = display.getPlayerAnimator3rd();
        if (animator3rd == null) {
            return;
        }
        if (!PlayerAnimatorAssetManager.get().containsKey(animator3rd)) {
            return;
        }
        PlayerAnimatorAssetManager.get().getAnimations(animator3rd, animationName).ifPresent(animation -> {
            var animLayer = PlayerAnimationAccess.getPlayerAnimationLayer(player, dataId);
            if (!(animLayer instanceof ModifierLayer<?>)) {
                return;
            }
            @SuppressWarnings("unchecked")
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) animLayer;
            IAnimation currentAnim = modifierLayer.getAnimation();
            if (currentAnim instanceof PlayerAnimationController controller && controller.isActive()) {
                Animation currentAnimation = controller.getCurrentAnimationInstance();
                if (currentAnimation != null) {
                    String currentName = currentAnimation.getNameOrId();
                    if (currentName != null && !animationName.equals(currentName)) {
                        AbstractFadeModifier fadeModifier = AbstractFadeModifier.standardFadeIn(8, EasingType.EASE_IN_OUT_SINE);
                        PlayerAnimationController newController = createController(player, animation);
                        modifierLayer.replaceAnimationWithFade(fadeModifier, newController);
                    }
                }
                return;
            }
            AbstractFadeModifier fadeModifier = AbstractFadeModifier.standardFadeIn(8, EasingType.EASE_IN_OUT_SINE);
            PlayerAnimationController newController = createController(player, animation);
            modifierLayer.replaceAnimationWithFade(fadeModifier, newController);
        });
    }

    @SuppressWarnings("unchecked")
    public static void playOnceAnimation(AbstractClientPlayer player, GunDisplayInstance display, Identifier dataId, String animationName) {
        Identifier animator3rd = display.getPlayerAnimator3rd();
        if (animator3rd == null) {
            return;
        }
        if (!PlayerAnimatorAssetManager.get().containsKey(animator3rd)) {
            return;
        }
        PlayerAnimatorAssetManager.get().getAnimations(animator3rd, animationName).ifPresent(animation -> {
            var animLayer = PlayerAnimationAccess.getPlayerAnimationLayer(player, dataId);
            if (!(animLayer instanceof ModifierLayer<?>)) {
                return;
            }
            @SuppressWarnings("unchecked")
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) animLayer;
            IAnimation currentAnim = modifierLayer.getAnimation();
            if (currentAnim == null || !currentAnim.isActive()) {
                AbstractFadeModifier fadeModifier = AbstractFadeModifier.standardFadeIn(8, EasingType.EASE_IN_OUT_SINE);
                PlayerAnimationController newController = createController(player, animation);
                modifierLayer.replaceAnimationWithFade(fadeModifier, newController);
            }
        });
    }

    public static void stopAllAnimation(AbstractClientPlayer player) {
        stopAllAnimation(player, 8);
    }

    public static void stopAllAnimation(AbstractClientPlayer player, int fadeTime) {
        stopAnimation(player, PlayerAnimatorCompat.LOWER_ANIMATION, fadeTime);
        stopAnimation(player, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, fadeTime);
        stopAnimation(player, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, fadeTime);
        stopAnimation(player, PlayerAnimatorCompat.ROTATION_ANIMATION, fadeTime);
    }


    @SuppressWarnings("unchecked")
    private static void stopAnimation(AbstractClientPlayer player, Identifier dataId, int fadeTime) {
        var animLayer = PlayerAnimationAccess.getPlayerAnimationLayer(player, dataId);
        if (animLayer instanceof ModifierLayer<?> && animLayer.isActive()) {
            ModifierLayer<IAnimation> modifierLayer = (ModifierLayer<IAnimation>) animLayer;
            AbstractFadeModifier fadeModifier = AbstractFadeModifier.standardFadeIn(fadeTime, EasingType.EASE_IN_OUT_SINE);
            modifierLayer.replaceAnimationWithFade(fadeModifier, null);
        }
    }

    private static boolean isPlayerLie(AbstractClientPlayer player) {
        // MOJANG's design: the lying down pose name is SWIMMING
        return !player.isSwimming() && player.getPose() == Pose.SWIMMING;
    }

    /**
     * Creates a PlayerAnimationController that immediately triggers the given animation.
     */
    private static PlayerAnimationController createController(AbstractClientPlayer player, Animation animation) {
        PlayerAnimationController controller = new PlayerAnimationController(
                player,
                (ctrl, data, setter) -> PlayState.CONTINUE
        );
        controller.triggerAnimation(animation);
        return controller;
    }

    public void onFire(GunShootEvent event) {
        if (event.getLogicalSide().isServer()) {
            return;
        }
        LivingEntity shooter = event.getShooter();
        if (!(shooter instanceof AbstractClientPlayer player)) {
            return;
        }
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player == player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        }
        ItemStack gunItemStack = event.getGunItemStack();
        IGun iGun = IGun.getIGunOrNull(gunItemStack);
        if (iGun == null) {
            return;
        }
        TimelessAPI.getGunDisplay(gunItemStack).ifPresent(index -> {
            IGunOperator operator = IGunOperator.fromLivingEntity(player);
            float aimingProgress = operator.getSynAimingProgress();
            if (aimingProgress <= 0) {
                if (isPlayerLie(player)) {
                    playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.LIE_NORMAL_FIRE);
                } else {
                    playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.NORMAL_FIRE_UPPER);
                }
            } else {
                if (isPlayerLie(player)) {
                    playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.LIE_AIM_FIRE);
                } else {
                    playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.AIM_FIRE_UPPER);
                }
            }
        });
    }

    public void onReload(GunReloadEvent event) {
        if (event.getLogicalSide().isServer()) {
            return;
        }
        LivingEntity shooter = event.getEntity();
        if (!(shooter instanceof AbstractClientPlayer player)) {
            return;
        }
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player == player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        }
        ItemStack gunItemStack = event.getGunItemStack();
        IGun iGun = IGun.getIGunOrNull(gunItemStack);
        if (iGun == null) {
            return;
        }
        TimelessAPI.getGunDisplay(gunItemStack).ifPresent(index -> {
            if (isPlayerLie(player)) {
                playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.LIE_RELOAD);
            } else {
                playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, AnimationName.RELOAD_UPPER);
            }
        });
    }

    public void onMelee(GunMeleeEvent event) {
        if (event.getLogicalSide().isServer()) {
            return;
        }
        LivingEntity shooter = event.getShooter();
        if (!(shooter instanceof AbstractClientPlayer player)) {
            return;
        }
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player == player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        }
        ItemStack gunItemStack = event.getGunItemStack();
        IGun iGun = IGun.getIGunOrNull(gunItemStack);
        if (iGun == null) {
            return;
        }
        int randomIndex = shooter.getRandom().nextInt(3);
        String animationName = switch (randomIndex) {
            case 0 -> AnimationName.MELEE_UPPER;
            case 1 -> AnimationName.MELEE_2_UPPER;
            default -> AnimationName.MELEE_3_UPPER;
        };
        TimelessAPI.getGunDisplay(gunItemStack).ifPresent(
                index -> playOnceAnimation(player, index, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, animationName)
        );
    }

    public void onDraw(GunDrawEvent event) {
        if (event.getLogicalSide().isServer()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer player)) {
            return;
        }
        if (Minecraft.getInstance().player != null && Minecraft.getInstance().player == player) {
            if (Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;
        }
        ItemStack currentGunItem = event.getCurrentGunItem();
        ItemStack previousGunItem = event.getPreviousGunItem();
        // When switching guns, reset upper body animation
        if (currentGunItem.getItem() instanceof IGun && previousGunItem.getItem() instanceof IGun) {
            stopAnimation(player, PlayerAnimatorCompat.LOOP_UPPER_ANIMATION, 8);
            stopAnimation(player, PlayerAnimatorCompat.ONCE_UPPER_ANIMATION, 8);
            stopAnimation(player, PlayerAnimatorCompat.LOWER_ANIMATION, 8);
        }
    }
}
