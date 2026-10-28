package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.VirtualOemAttachment;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ClientMessageInstallVirtualAttachment implements FabricPacket {
    public static final PacketType<ClientMessageInstallVirtualAttachment> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_install_virtual_attachment"), ClientMessageInstallVirtualAttachment::new);

    private final int gunSlotIndex;
    private final AttachmentType attachmentType;
    private final ResourceLocation attachmentId;

    public ClientMessageInstallVirtualAttachment(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readEnum(AttachmentType.class), buf.readResourceLocation());
    }

    public ClientMessageInstallVirtualAttachment(int gunSlotIndex, AttachmentType attachmentType, ResourceLocation attachmentId) {
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
        this.attachmentId = attachmentId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(gunSlotIndex);
        buf.writeEnum(attachmentType);
        buf.writeResourceLocation(attachmentId);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        if (player == null) {
            return;
        }
        Inventory inventory = player.getInventory();
        ItemStack gunItem = inventory.getItem(gunSlotIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun == null || iGun.hasAttachmentLock(gunItem)) {
            return;
        }
        if (!VirtualOemAttachment.isAllowedVirtualOem(iGun.getGunId(gunItem), attachmentId, attachmentType)) {
            return;
        }
        ItemStack virtualAttachment = AttachmentItemBuilder.create().setId(attachmentId).build();
        IAttachment iAttachment = IAttachment.getIAttachmentOrNull(virtualAttachment);
        if (iAttachment == null || iAttachment.getType(virtualAttachment) != attachmentType || !iGun.allowAttachment(gunItem, virtualAttachment)) {
            return;
        }
        ItemStack oldAttachmentItem = iGun.getAttachment(gunItem, attachmentType);
        if (!oldAttachmentItem.isEmpty() && !VirtualOemAttachment.isMarked(oldAttachmentItem) && inventory.getFreeSlot() == -1) {
            return;
        }
        VirtualOemAttachment.mark(virtualAttachment);
        ItemStack beforeRefit = gunItem.copy();
        iGun.installAttachment(gunItem, virtualAttachment);
        GunRefitTrigger.INSTANCE.trigger(player, beforeRefit, gunItem);
        if (!oldAttachmentItem.isEmpty() && !VirtualOemAttachment.isMarked(oldAttachmentItem)) {
            inventory.add(oldAttachmentItem);
        }
        AttachmentPropertyManager.postChangeEvent(player, gunItem);
        player.inventoryMenu.broadcastChanges();
        NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
    }
}
