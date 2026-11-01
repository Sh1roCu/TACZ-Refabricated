package cn.sh1rocu.tacz.util.itemhandler;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {
    T serializeNBT();

    void deserializeNBT(T var1);
}
