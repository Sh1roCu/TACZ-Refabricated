package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.advancement.GunRefitTrigger;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
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

public class ClientMessageRefitGun implements FabricPacket {
    public static final PacketType<ClientMessageRefitGun> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_player_refit"), ClientMessageRefitGun::new);

    private final int attachmentSlotIndex;
    private final int gunSlotIndex;
    private final AttachmentType attachmentType;

    public ClientMessageRefitGun(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readEnum(AttachmentType.class));
    }

    public ClientMessageRefitGun(int attachmentSlotIndex, int gunSlotIndex, AttachmentType attachmentType) {
        this.attachmentSlotIndex = attachmentSlotIndex;
        this.gunSlotIndex = gunSlotIndex;
        this.attachmentType = attachmentType;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(attachmentSlotIndex);
        buf.writeInt(gunSlotIndex);
        buf.writeEnum(attachmentType);
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
        ItemStack attachmentItem = inventory.getItem(attachmentSlotIndex);
        ItemStack gunItem = inventory.getItem(gunSlotIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            // 服务端校验配件锁
            if (iGun.hasAttachmentLock(gunItem)) {
                return;
            }
            if (iGun.allowAttachment(gunItem, attachmentItem)) {
                // 使用配件物品自身的真实类型，而非客户端传入的 attachmentType
                IAttachment iAttachment = IAttachment.getIAttachmentOrNull(attachmentItem);
                if (iAttachment == null) {
                    return;
                }
                AttachmentType realType = iAttachment.getType(attachmentItem);
                ItemStack oldAttachmentItem = iGun.getAttachment(gunItem, realType);
                ItemStack beforeRefit = gunItem.copy();
                iGun.installAttachment(gunItem, attachmentItem);
                GunRefitTrigger.INSTANCE.trigger(player, beforeRefit, gunItem);
                // 刷新配件数据
                AttachmentPropertyManager.postChangeEvent(player, gunItem);
                inventory.setItem(attachmentSlotIndex, VirtualOemAttachment.isMarked(oldAttachmentItem) ? ItemStack.EMPTY : oldAttachmentItem);
                // 如果卸载的是扩容弹匣，吐出所有子弹
                if (realType == AttachmentType.EXTENDED_MAG) {
                    iGun.dropAllAmmo(player, gunItem);
                }
                player.inventoryMenu.broadcastChanges();
                NetworkHandler.sendToClientPlayer(new ServerMessageRefreshRefitScreen(), player);
            }
        }
    }
}
