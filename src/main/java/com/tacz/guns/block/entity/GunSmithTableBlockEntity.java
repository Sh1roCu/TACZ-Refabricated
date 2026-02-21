package com.tacz.guns.block.entity;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory<Identifier> {
    public static final BlockEntityType<GunSmithTableBlockEntity> TYPE = net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.create(GunSmithTableBlockEntity::new,
            ModBlocks.GUN_SMITH_TABLE,
            ModBlocks.WORKBENCH_111,
            ModBlocks.WORKBENCH_121,
            ModBlocks.WORKBENCH_211
    ).build();

    private static final String ID_TAG = "BlockId";

    @Nullable
    private Identifier id = null;

    public GunSmithTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    public void setId(Identifier id) {
        this.id = id;
    }

    @Nullable
    public Identifier getId() {
        return id;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // TODO
//    @Override
//    @Environment(EnvType.CLIENT)
//    public AABB getRenderBoundingBox() {
//        return AABB.encapsulatingFullBlocks(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 1, 2));
//    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Gun Smith Table");
    }

    @Override
    public Identifier getScreenOpeningData(ServerPlayer serverPlayer) {
        return this.getId() == null ? DefaultAssets.DEFAULT_BLOCK_ID : this.getId();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GunSmithTableMenu(id, inventory, getId());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getString(ID_TAG).ifPresentOrElse(
            s -> this.id = Identifier.tryParse(s),
            () -> this.id = DefaultAssets.DEFAULT_BLOCK_ID
        );
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (id != null) {
            output.putString(ID_TAG, id.toString());
        }
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
