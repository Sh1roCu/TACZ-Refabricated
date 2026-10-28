package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.VariantHelper;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClientMessageSetAttachmentVariant implements FabricPacket {
    public static final PacketType<ClientMessageSetAttachmentVariant> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_set_attachment_variant"), ClientMessageSetAttachmentVariant::new);

    private final int gunSlotIndex;
    private final AttachmentType attachmentType;
    @Nullable
    private final ResourceLocation variantId;

    public ClientMessageSetAttachmentVariant(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readEnum(AttachmentType.class), buf.readResourceLocation());
    }

    public ClientMessageSetAttachmentVariant(int gunSlotIndex, AttachmentType attachmentType, @Nullable ResourceLocation variantId) {
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
        this.variantId = variantId;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(gunSlotIndex);
        buf.writeEnum(attachmentType);
        buf.writeBoolean(variantId != null);
        if (variantId != null) {
            buf.writeResourceLocation(variantId);
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
        if (player == null) {
            return;
        }
        Inventory inventory = player.getInventory();
        ItemStack gunItem = inventory.getItem(gunSlotIndex);
        IGun gun = IGun.getIGunOrNull(gunItem);
        if (gun == null || gun.hasAttachmentLock(gunItem)) {
            return;
        }
        ItemStack attachmentItem = gun.getAttachment(gunItem, attachmentType);
        IAttachment attachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        if (attachment == null || attachmentItem.isEmpty()) {
            return;
        }
        ItemStack beforeRefit = gunItem.copy();
        ResourceLocation attachmentId = attachment.getAttachmentId(attachmentItem);
        if (variantId == null) {
            VariantHelper.removeVariant(attachmentItem);
        } else {
            boolean valid = com.tacz.guns.api.TimelessAPI.getCommonAttachmentVariantIndex(variantId)
                    .map(index -> index.appliesTo(attachmentId))
                    .orElse(false);
            if (!valid) {
                return;
            }
            VariantHelper.setVariant(attachmentItem, variantId);
        }
        gun.installAttachment(gunItem, attachmentItem);
        GunRefitTrigger.INSTANCE.trigger(player, beforeRefit, gunItem);
        AttachmentPropertyManager.postChangeEvent(player, gunItem);
        player.inventoryMenu.broadcastChanges();
        NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
    }
}
