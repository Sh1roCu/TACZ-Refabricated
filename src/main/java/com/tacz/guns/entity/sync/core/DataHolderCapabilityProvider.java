package com.tacz.guns.entity.sync.core;

import cn.sh1rocu.tacz.util.forge.LazyOptional;
import com.tacz.guns.GunMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;

import java.util.Optional;

public class DataHolderCapabilityProvider implements Component {
    public static final ComponentKey<DataHolderCapabilityProvider> CAPABILITY = ComponentRegistry.getOrCreate(Identifier.fromNamespaceAndPath(GunMod.MOD_ID, "synced_entity_data"), DataHolderCapabilityProvider.class);
    private final DataHolder holder = new DataHolder();
    private final LazyOptional<DataHolder> optional = LazyOptional.of(() -> this.holder);

    public void invalidate() {
        this.optional.invalidate();
    }

    public Optional<DataHolder> getDataHolder() {
        return optional.resolve();
    }

    private CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag result = new CompoundTag();
        ListTag list = new ListTag();
        this.holder.dataMap.forEach((key, entry) -> {
            if (key.save()) {
                CompoundTag keyTag = new CompoundTag();
                keyTag.putString("ClassKey", key.classKey().id().toString());
                keyTag.putString("DataKey", key.id().toString());
                keyTag.put("Value", entry.writeValue(provider));
                list.add(keyTag);
            }
        });
        result.put("DataHolder", list);
        return result;
    }

    private void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.holder.dataMap.clear();
        ListTag listTag = tag.getListOrEmpty("DataHolder");
        listTag.forEach(entryTag -> {
            CompoundTag keyTag = (CompoundTag) entryTag;
            Identifier classKey = Identifier.tryParse(keyTag.getStringOr("ClassKey", ""));
            Identifier dataKey = Identifier.tryParse(keyTag.getStringOr("DataKey", ""));
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
            entry.readValue(provider, value);
            this.holder.dataMap.put(syncedDataKey, entry);
        });
    }

    @Override
    public void readData(@NotNull ValueInput input) {
        input.read("tacz_synced_data", CompoundTag.CODEC).ifPresent(tag -> {
            deserializeNBT(input.lookup(), tag);
        });
    }

    @Override
    public void writeData(@NotNull ValueOutput output) {
        CompoundTag tag = serializeNBT(RegistryAccess.EMPTY);
        output.store("tacz_synced_data", CompoundTag.CODEC, tag);
    }
}
