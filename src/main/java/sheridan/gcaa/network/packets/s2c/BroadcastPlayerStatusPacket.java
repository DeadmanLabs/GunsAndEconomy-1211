package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record BroadcastPlayerStatusPacket(int id, long lastShoot, long lastChamberAction, long localTimeOffset, int latency, long balance, boolean reloading) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BroadcastPlayerStatusPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "broadcast_player_status"));

    public static final StreamCodec<ByteBuf, BroadcastPlayerStatusPacket> CODEC = new StreamCodec<>() {
        @Override
        public BroadcastPlayerStatusPacket decode(ByteBuf buf) {
            int id = ByteBufCodecs.VAR_INT.decode(buf);
            long lastShoot = ByteBufCodecs.VAR_LONG.decode(buf);
            long lastChamberAction = ByteBufCodecs.VAR_LONG.decode(buf);
            long localTimeOffset = ByteBufCodecs.VAR_LONG.decode(buf);
            int latency = ByteBufCodecs.VAR_INT.decode(buf);
            long balance = ByteBufCodecs.VAR_LONG.decode(buf);
            boolean reloading = ByteBufCodecs.BOOL.decode(buf);
            return new BroadcastPlayerStatusPacket(id, lastShoot, lastChamberAction, localTimeOffset, latency, balance, reloading);
        }

        @Override
        public void encode(ByteBuf buf, BroadcastPlayerStatusPacket packet) {
            ByteBufCodecs.VAR_INT.encode(buf, packet.id);
            ByteBufCodecs.VAR_LONG.encode(buf, packet.lastShoot);
            ByteBufCodecs.VAR_LONG.encode(buf, packet.lastChamberAction);
            ByteBufCodecs.VAR_LONG.encode(buf, packet.localTimeOffset);
            ByteBufCodecs.VAR_INT.encode(buf, packet.latency);
            ByteBufCodecs.VAR_LONG.encode(buf, packet.balance);
            ByteBufCodecs.BOOL.encode(buf, packet.reloading);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BroadcastPlayerStatusPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateClientPlayerStatus(
                    packet.id,
                    packet.lastShoot,
                    packet.lastChamberAction,
                    packet.localTimeOffset,
                    packet.latency,
                    packet.balance,
                    packet.reloading
            );
        });
    }
}
