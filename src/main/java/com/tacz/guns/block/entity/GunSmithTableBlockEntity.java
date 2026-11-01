package com.tacz.guns.block.entity;

import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.inventory.GunSmithTableMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GunSmithTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    public static final BlockEntityType<GunSmithTableBlockEntity> TYPE = BlockEntityType.Builder.of(GunSmithTableBlockEntity::new,
            ModBlocks.GUN_SMITH_TABLE,
            ModBlocks.WORKBENCH_111,
            ModBlocks.WORKBENCH_121,
            ModBlocks.WORKBENCH_211
    ).build(null);

    private static final String ID_TAG = "BlockId";

    @Nullable
    private ResourceLocation id = null;

    public GunSmithTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    @Nullable
    public ResourceLocation getId() {
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
//        return new AABB(worldPosition.offset(-2, 0, -2), worldPosition.offset(2, 1, 2));
//    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Gun Smith Table");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer serverPlayer, FriendlyByteBuf buf) {
        ResourceLocation rl = this.getId() == null ? DefaultAssets.DEFAULT_BLOCK_ID : this.getId();
        buf.writeResourceLocation(rl);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new GunSmithTableMenu(id, inventory, getId());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(ID_TAG, Tag.TAG_STRING)) {
            this.id = ResourceLocation.tryParse(tag.getString(ID_TAG));
        } else {
            this.id = DefaultAssets.DEFAULT_BLOCK_ID;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (id != null) {
            tag.putString(ID_TAG, id.toString());
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }
}
