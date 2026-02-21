package cn.sh1rocu.tacz.mixin.common.itemhandler;

import cn.sh1rocu.tacz.util.forge.LazyOptional;
import cn.sh1rocu.tacz.util.itemhandler.CombinedInvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.IItemHandler;
import cn.sh1rocu.tacz.util.itemhandler.entity.player.PlayerArmorInvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.entity.player.PlayerInvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.entity.player.PlayerMainInvWrapper;
import cn.sh1rocu.tacz.util.itemhandler.entity.player.PlayerOffhandInvWrapper;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow
    @Final
    Inventory inventory;
    @Unique
    private LazyOptional<IItemHandler> playerMainHandler;
    @Unique
    private LazyOptional<IItemHandler> playerEquipmentHandler;
    @Unique
    private LazyOptional<IItemHandler> playerJoinedHandler;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void tacz$initClass(Level world, GameProfile gameProfile, CallbackInfo ci) {
        this.playerMainHandler = LazyOptional.of(() ->
                new PlayerMainInvWrapper(this.inventory));
        this.playerEquipmentHandler = LazyOptional.of(() ->
                new CombinedInvWrapper(new PlayerArmorInvWrapper(this.inventory), new PlayerOffhandInvWrapper(this.inventory)));
        this.playerJoinedHandler = LazyOptional.of(() ->
                new PlayerInvWrapper(this.inventory));
    }

    @Override
    public LazyOptional<IItemHandler> tacz$getItemHandler(@Nullable Direction facing) {
        if (isAlive()) {
            if (facing == null) {
                return this.playerJoinedHandler.cast();
            }

            if (facing.getAxis().isVertical()) {
                return this.playerMainHandler.cast();
            }

            if (facing.getAxis().isHorizontal()) {
                return this.playerEquipmentHandler.cast();
            }
        }

        return super.tacz$getItemHandler(facing);
    }
}
