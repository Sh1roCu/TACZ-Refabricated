package com.tacz.guns.network.message;

import com.tacz.guns.client.particle.HitBlockEffectHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ServerMessageBulletHitBlock implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerMessageBulletHitBlock> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("tacz", "server_message_bullet_hit_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerMessageBulletHitBlock> STREAM_CODEC =
            StreamCodec.of((buf, message) -> encode(message, buf), ServerMessageBulletHitBlock::decode);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final double hitX;
    private final double hitY;
    private final double hitZ;
    private final Direction direction;
    private final BlockPos blockPos;
    private final String ammoId;
    private final String gunId;
    private final String gunDisplayId;
    private final boolean incendiary;

    public ServerMessageBulletHitBlock(Vec3 hitPos, Direction direction, BlockPos blockPos, String ammoId, String gunId, String gunDisplayId, boolean incendiary) {
        this.hitX = hitPos.x;
        this.hitY = hitPos.y;
        this.hitZ = hitPos.z;
        this.direction = direction;
        this.blockPos = blockPos;
        this.ammoId = ammoId;
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
        this.incendiary = incendiary;
    }

    public ServerMessageBulletHitBlock(double hitX, double hitY, double hitZ, Direction direction, BlockPos blockPos, String ammoId, String gunId, String gunDisplayId, boolean incendiary) {
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitZ = hitZ;
        this.direction = direction;
        this.blockPos = blockPos;
        this.ammoId = ammoId;
        this.gunId = gunId;
        this.gunDisplayId = gunDisplayId;
        this.incendiary = incendiary;
    }

    public static void encode(ServerMessageBulletHitBlock message, RegistryFriendlyByteBuf buf) {
        buf.writeDouble(message.hitX);
        buf.writeDouble(message.hitY);
        buf.writeDouble(message.hitZ);
        buf.writeEnum(message.direction);
        buf.writeBlockPos(message.blockPos);
        buf.writeUtf(message.ammoId);
        buf.writeUtf(message.gunId);
        buf.writeUtf(message.gunDisplayId);
        buf.writeBoolean(message.incendiary);
    }

    public static ServerMessageBulletHitBlock decode(RegistryFriendlyByteBuf buf) {
        double hitX = buf.readDouble();
        double hitY = buf.readDouble();
        double hitZ = buf.readDouble();
        Direction direction = buf.readEnum(Direction.class);
        BlockPos blockPos = buf.readBlockPos();
        String ammoId = buf.readUtf();
        String gunId = buf.readUtf();
        String gunDisplayId = buf.readUtf();
        boolean incendiary = buf.readBoolean();
        return new ServerMessageBulletHitBlock(hitX, hitY, hitZ, direction, blockPos, ammoId, gunId, gunDisplayId, incendiary);
    }

    @Environment(EnvType.CLIENT)
    public static void handle(ServerMessageBulletHitBlock message, ClientPlayNetworking.Context context) {
        context.client().execute(() -> handleClient(message));
    }

    @Environment(EnvType.CLIENT)
    private static void handleClient(ServerMessageBulletHitBlock message) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        HitBlockEffectHandler.handle(level,
                new Vec3(message.hitX, message.hitY, message.hitZ),
                message.direction,
                message.blockPos,
                message.ammoId,
                message.gunId,
                message.gunDisplayId,
                message.incendiary);
    }
}