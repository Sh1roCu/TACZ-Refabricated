package com.tacz.guns.entity.sync.core;

import cn.sh1rocu.tacz.util.forge.LazyOptional;
import com.tacz.guns.GunMod;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DataHolderCapabilityProvider implements Component {
    public static final ComponentKey<DataHolderCapabilityProvider> CAPABILITY = ComponentRegistry.getOrCreate(new ResourceLocation(GunMod.MOD_ID, "synced_entity_data"), DataHolderCapabilityProvider.class);
    private final DataHolder holder = new DataHolder();
    private final LazyOptional<DataHolder> optional = LazyOptional.of(() -> this.holder);

    public void invalidate() {
        this.optional.invalidate();
    }

    public Optional<DataHolder> getDataHolder() {
        return optional.resolve();
    }

    private ListTag serializeNBT() {
        ListTag list = new ListTag();
        this.holder.dataMap.forEach((key, entry) -> {
            if (key.save()) {
                CompoundTag keyTag = new CompoundTag();
                keyTag.putString("ClassKey", key.classKey().id().toString());
                keyTag.putString("DataKey", key.id().toString());
                keyTag.put("Value", entry.writeValue());
                list.add(keyTag);
            }
        });
        return list;
    }

    private void deserializeNBT(ListTag listTag) {
        this.holder.dataMap.clear();
        listTag.forEach(entryTag -> {
            CompoundTag keyTag = (CompoundTag) entryTag;
            ResourceLocation classKey = ResourceLocation.tryParse(keyTag.getString("ClassKey"));
            ResourceLocation dataKey = ResourceLocation.tryParse(keyTag.getString("DataKey"));
            Tag value = keyTag.get("Value");
            SyncedClassKey<?> syncedClassKey = SyncedEntityData.instance().getClassKey(classKey);
            if (syncedClassKey == null) {
                return;
            }
            SyncedDataKey<?, ?> syncedDataKey = SyncedEntityData.instance().getKey(syncedClassKey, dataKey);
            if (syncedDataKey == null || !syncedDataKey.save()) {
                return;
            }
            DataEntry<?, ?> entry = new DataEntry<>(syncedDataKey);
            entry.readValue(value);
            this.holder.dataMap.put(syncedDataKey, entry);
        });
    }

    @Override
    public void readFromNbt(@NotNull CompoundTag tag) {
        deserializeNBT(tag.getList("DataHolder", Tag.TAG_COMPOUND));
    }

    @Override
    public void writeToNbt(@NotNull CompoundTag tag) {
        ListTag listTag = serializeNBT();
        tag.put("DataHolder", listTag);
    }
}
