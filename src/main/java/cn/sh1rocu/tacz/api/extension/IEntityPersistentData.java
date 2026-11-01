package cn.sh1rocu.tacz.api.extension;

import net.minecraft.nbt.CompoundTag;

public interface IEntityPersistentData {
    default CompoundTag tacz$getPersistentData() {
        throw new RuntimeException();
    }
}