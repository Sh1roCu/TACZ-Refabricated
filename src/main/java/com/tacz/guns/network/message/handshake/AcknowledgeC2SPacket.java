package com.tacz.guns.network.message.handshake;

import com.tacz.guns.GunMod;
import com.tacz.guns.network.IHandshakeMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class AcknowledgeC2SPacket implements IHandshakeMessage.IResponsePacket {
    public static final Marker ACKNOWLEDGE = MarkerFactory.getMarker("HANDSHAKE_ACKNOWLEDGE");
    public static final ResourceLocation ID = new ResourceLocation(GunMod.MOD_ID, "acknowledge");

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public void read(FriendlyByteBuf buf) {
    }

    @Override
    public void handle(PacketSender sender) {
        GunMod.LOGGER.debug(ACKNOWLEDGE, "Received acknowledgement from client");
    }

    public ResourceLocation getId() {
        return ID;
    }
}
