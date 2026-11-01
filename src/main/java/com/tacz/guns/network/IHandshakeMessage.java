package com.tacz.guns.network;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IHandshakeMessage extends FabricPacket {
    @Nullable IResponsePacket handle(Connection connection, Consumer<GenericFutureListener<? extends Future<? super Void>>> consumer);

    interface IResponsePacket {
        void write(FriendlyByteBuf buf);

        void read(FriendlyByteBuf buf);

        void handle(PacketSender sender);

        ResourceLocation getId();
    }
}
