package com.tacz.guns.network.message;

import com.tacz.guns.GunMod;
import com.tacz.guns.client.gui.GunRefitScreen;
import com.tacz.guns.resource.modifier.AttachmentPropertyManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ServerMessageRefreshRefitScreen implements CustomPacketPayload {
    public static final ServerMessageRefreshRefitScreen INSTANCE = new ServerMessageRefreshRefitScreen();
    public static final CustomPacketPayload.Type<ServerMessageRefreshRefitScreen> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "server_refresh_refit_screen")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageRefreshRefitScreen> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageRefreshRefitScreen message, ClientPlayNetworking.Context context) {
        context.client().execute(ServerMessageRefreshRefitScreen::updateScreen);
    }

    @Environment(EnvType.CLIENT)
    private static void updateScreen() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && Minecraft.getInstance().screen instanceof GunRefitScreen screen) {
            screen.init();
            // 刷新配件数据，客户端的
            AttachmentPropertyManager.postChangeEvent(player, player.getMainHandItem());
        }
    }
}