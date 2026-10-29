package com.tacz.guns.network.message;

import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.network.NetworkHandler;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import com.tacz.guns.util.VariantHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ClientMessageSetAttachmentVariant implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClientMessageSetAttachmentVariant> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "client_message_set_attachment_variant"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientMessageSetAttachmentVariant> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ClientMessageSetAttachmentVariant::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int gunSlotIndex;
    private final AttachmentType attachmentType;
    @Nullable
    private final ResourceLocation variantId;

    public ClientMessageSetAttachmentVariant(int gunSlotIndex, AttachmentType attachmentType, @Nullable ResourceLocation variantId) {
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
        this.variantId = variantId;
    }

    public static void encode(ClientMessageSetAttachmentVariant message, RegistryFriendlyByteBuf buf) {
        buf.writeInt(message.gunSlotIndex);
        buf.writeEnum(message.attachmentType);
        buf.writeBoolean(message.variantId != null);
        if (message.variantId != null) {
            buf.writeResourceLocation(message.variantId);
        }
    }

    public static ClientMessageSetAttachmentVariant decode(RegistryFriendlyByteBuf buf) {
        int gunSlotIndex = buf.readInt();
        AttachmentType attachmentType = buf.readEnum(AttachmentType.class);
        ResourceLocation variantId = buf.readBoolean() ? buf.readResourceLocation() : null;
        return new ClientMessageSetAttachmentVariant(gunSlotIndex, attachmentType, variantId);
    }

    public static void handle(ClientMessageSetAttachmentVariant message, ServerPlayNetworking.Context context) {
        context.server().execute(() -> handleServer(message, context.player()));
    }

    private static void handleServer(ClientMessageSetAttachmentVariant message, @Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }
        Inventory inventory = player.getInventory();
        ItemStack gunItem = inventory.getItem(message.gunSlotIndex);
        IGun gun = IGun.getIGunOrNull(gunItem);
        if (gun == null || gun.hasAttachmentLock(gunItem)) {
            return;
        }
        ItemStack attachmentItem = gun.getAttachment(player.registryAccess(), gunItem, message.attachmentType);
        IAttachment attachment = IAttachment.getIAttachmentOrNull(attachmentItem);
        if (attachment == null || attachmentItem.isEmpty()) {
            return;
        }
        ItemStack beforeRefit = gunItem.copy();
        ResourceLocation attachmentId = attachment.getAttachmentId(attachmentItem);
        if (message.variantId == null) {
            VariantHelper.removeVariant(attachmentItem);
        } else {
            boolean valid = com.tacz.guns.api.TimelessAPI.getCommonAttachmentVariantIndex(message.variantId)
                    .map(index -> index.appliesTo(attachmentId))
                    .orElse(false);
            if (!valid) {
                return;
            }
            VariantHelper.setVariant(attachmentItem, message.variantId);
        }
        gun.installAttachment(player.registryAccess(), gunItem, attachmentItem);
        GunRefitTrigger.INSTANCE.trigger(player, beforeRefit, gunItem);
        AttachmentPropertyManager.postChangeEvent(player, gunItem);
        player.inventoryMenu.broadcastChanges();
        NetworkHandler.sendToClientPlayer(ServerMessageRefreshRefitScreen.INSTANCE, player);
    }
}