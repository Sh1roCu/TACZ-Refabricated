package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClientMessageLaserColor implements FabricPacket {
    public static final PacketType<ClientMessageLaserColor> TYPE = PacketType.create(new ResourceLocation(GunMod.MOD_ID, "c2s_laser_color"), ClientMessageLaserColor::new);

    private final Map<AttachmentType, Integer> colorMap = new HashMap<>();
    private boolean applyGunColor = false;
    private int gunColor = 0;

    private int gunSlotIndex = -1;

    private ClientMessageLaserColor() {

    }

    public ClientMessageLaserColor(FriendlyByteBuf buf) {
        this.colorMap.putAll(buf.readMap(buf1 -> buf.readEnum(AttachmentType.class), FriendlyByteBuf::readInt));
        this.applyGunColor = buf.readBoolean();
        this.gunColor = buf.readInt();
        this.gunSlotIndex = buf.readInt();
    }

    public ClientMessageLaserColor(@NotNull ItemStack gun, int gunSlotIndex) {
        if (gun.getItem() instanceof IGun iGun) {
            for (AttachmentType type : AttachmentType.values()) {
                ItemStack attachment = iGun.getAttachment(gun, type);
                if (attachment.getItem() instanceof IAttachment iAttachment) {
                    if (iAttachment.hasCustomLaserColor(attachment)) {
                        colorMap.put(type, iAttachment.getLaserColor(attachment));
                    }
                }
            }
            if (iGun.hasCustomLaserColor(gun)) {
                this.gunColor = iGun.getLaserColor(gun);
                this.applyGunColor = true;
            }
            this.gunSlotIndex = gunSlotIndex;
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeMap(colorMap, FriendlyByteBuf::writeEnum, FriendlyByteBuf::writeInt);
        buf.writeBoolean(applyGunColor);
        buf.writeInt(gunColor);
        buf.writeInt(gunSlotIndex);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    public void handle(ServerPlayer player, PacketSender responseSender) {
        Inventory inventory = player.getInventory();
        ItemStack gunItem = inventory.getItem(gunSlotIndex);
        IGun iGun = IGun.getIGunOrNull(gunItem);
        if (iGun != null) {
            for (var entry : colorMap.entrySet()) {
                AttachmentType type = entry.getKey();
                int color = entry.getValue();
                ItemStack attachment = iGun.getAttachment(gunItem, type);
                if (attachment.getItem() instanceof IAttachment iAttachment) {
                    iAttachment.setLaserColor(attachment, color);
                }
            }
            if (applyGunColor) {
                iGun.setLaserColor(gunItem, gunColor);
            }
        }
    }
}
