package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record UpdateTransferBalancePacket(long balance) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateTransferBalancePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_transfer_balance"));

    public static final StreamCodec<ByteBuf, UpdateTransferBalancePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, UpdateTransferBalancePacket::balance,
        UpdateTransferBalancePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateTransferBalancePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateTransferBalance(packet.balance);
        });
    }
}
