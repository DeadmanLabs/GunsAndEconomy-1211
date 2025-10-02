package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.Clients;

public record ClientHitBlockPacket(BlockPos blockPos, Vector3f pos, int directionIndex, int[] modsIndexList) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientHitBlockPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "client_hit_block"));

    // Custom StreamCodec for int array
    private static final StreamCodec<ByteBuf, int[]> INT_ARRAY_CODEC = new StreamCodec<>() {
        @Override
        public int[] decode(ByteBuf buf) {
            int length = ByteBufCodecs.VAR_INT.decode(buf);
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = ByteBufCodecs.VAR_INT.decode(buf);
            }
            return array;
        }

        @Override
        public void encode(ByteBuf buf, int[] array) {
            ByteBufCodecs.VAR_INT.encode(buf, array.length);
            for (int value : array) {
                ByteBufCodecs.VAR_INT.encode(buf, value);
            }
        }
    };

    public static final StreamCodec<ByteBuf, ClientHitBlockPacket> CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, ClientHitBlockPacket::blockPos,
        ByteBufCodecs.VECTOR3F, ClientHitBlockPacket::pos,
        ByteBufCodecs.VAR_INT, ClientHitBlockPacket::directionIndex,
        INT_ARRAY_CODEC, ClientHitBlockPacket::modsIndexList,
        ClientHitBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientHitBlockPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.onProjectileHitBlock(packet.blockPos, packet.pos, packet.directionIndex, packet.modsIndexList);
        });
    }
}
