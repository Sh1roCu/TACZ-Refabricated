package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.SlotAdapterHelper;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClientMessageSetSlotAdapter implements FabricPacket {
    public static final PacketType<ClientMessageSetSlotAdapter> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_set_slot_adapter"), ClientMessageSetSlotAdapter::new);

    private final int gunSlotIndex;
    private final AttachmentType attachmentType;
    @Nullable
    private final ResourceLocation adapterId;

    public ClientMessageSetSlotAdapter(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readEnum(AttachmentType.class), buf.readResourceLocation());
    }

    public ClientMessageSetSlotAdapter(int gunSlotIndex, AttachmentType attachmentType, @Nullable ResourceLocation adapterId) {
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
        this.adapterId = adapterId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(gunSlotIndex);
        buf.writeEnum(attachmentType);
        buf.writeBoolean(adapterId != null);
        if (adapterId != null) {
            buf.writeResourceLocation(adapterId);
        }
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        handleServer(player);
    }

    private void handleServer(@Nullable ServerPlayer player) {
        if (player == null || attachmentType == AttachmentType.NONE) {
            return;
        }
        Inventory inventory = player.getInventory();
        if (gunSlotIndex < 0 || gunSlotIndex >= inventory.getContainerSize()) {
            return;
        }
        ItemStack gunItem = inventory.getItem(gunSlotIndex);
        IGun gun = IGun.getIGunOrNull(gunItem);
        if (gun == null || gun.hasAttachmentLock(gunItem) || !gun.allowAttachmentType(gunItem, attachmentType)) {
            return;
        }

        ItemStack attachmentItem = gun.getAttachment(gunItem, attachmentType);
        ResourceLocation attachmentId = null;
        if (!attachmentItem.isEmpty()) {
            IAttachment attachment = IAttachment.getIAttachmentOrNull(attachmentItem);
            if (attachment == null || attachment.getType(attachmentItem) != attachmentType) {
                return;
            }
            attachmentId = attachment.getAttachmentId(attachmentItem);
        }

        ItemStack beforeRefit = gunItem.copy();
        if (adapterId == null) {
            if (attachmentId != null && !SlotAdapterHelper.allowsDirectAttachment(gunItem, attachmentId)) {
                return;
            }
            SlotAdapterHelper.removeSlotAdapter(gunItem, attachmentType);
        } else {
            ResourceLocation currentAttachmentId = attachmentId;
            boolean validSelection = SlotAdapterHelper.getAvailableSlotAdapters(gunItem, attachmentType).stream()
                    .filter(entry -> entry.getKey().equals(adapterId))
                    .anyMatch(entry -> currentAttachmentId == null || entry.getValue().allowsAttachment(currentAttachmentId));
            if (!validSelection) {
                return;
            }
            SlotAdapterHelper.setSlotAdapter(gunItem, attachmentType, adapterId);
        }

        AttachmentPropertyManager.postChangeEvent(player, gunItem);
        GunRefitTrigger.INSTANCE.trigger(player, beforeRefit, gunItem);
        player.inventoryMenu.broadcastChanges();
        NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
    }
}
