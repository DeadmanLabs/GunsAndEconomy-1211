package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.ModAttachments;
import sheridan.gcaa.capability.PlayerStatus;
import sheridan.gcaa.network.packets.s2c.BroadcastPlayerStatusPacket;

public record RequestDataSyncPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestDataSyncPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "request_data_sync"));

    public static final StreamCodec<ByteBuf, RequestDataSyncPacket> CODEC = StreamCodec.unit(new RequestDataSyncPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestDataSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerStatus cap = player.getData(ModAttachments.PLAYER_STATUS);
                if (cap != null) {
                    PacketDistributor.sendToPlayer(player,
                            new BroadcastPlayerStatusPacket(
                                    player.getId(),
                                    cap.getLastShoot(),
                                    cap.getLastChamberAction(),
                                    cap.getLocalTimeOffset(),
                                    cap.getLatency(),
                                    cap.getBalance(),
                                    cap.isReloading()));
                }
            }
        });
    }
}
