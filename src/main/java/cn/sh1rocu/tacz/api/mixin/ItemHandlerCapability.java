package cn.sh1rocu.tacz.api.mixin;

import cn.sh1rocu.tacz.util.neoforge.LazyOptional;
import cn.sh1rocu.tacz.util.itemhandler.IItemHandler;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface ItemHandlerCapability {

    default LazyOptional<IItemHandler> tacz$getItemHandler(@Nullable Direction facing) {
        return LazyOptional.empty();
    }

    default void tacz$invalidateItemHandler() {
    }

    default void tacz$reviveItemHandler() {
    }
}