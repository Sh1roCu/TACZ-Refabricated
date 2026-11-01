package cn.sh1rocu.tacz.api.mixin;

import net.minecraft.world.entity.Pose;

public interface ForcePoseInjection {
    Pose tacz$getForcedPose();

    void tacz$setForcedPose(Pose pose);
}