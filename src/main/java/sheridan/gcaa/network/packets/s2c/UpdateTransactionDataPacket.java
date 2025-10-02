package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

import java.util.ArrayList;
import java.util.List;

public record UpdateTransactionDataPacket(List<Integer> playerIds) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateTransactionDataPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_transaction_data"));

    public static final StreamCodec<ByteBuf, UpdateTransactionDataPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT), UpdateTransactionDataPacket::playerIds,
        UpdateTransactionDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateTransactionDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateTransactionTerminalScreenData(packet.playerIds);
        });
    }
}
