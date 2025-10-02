package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.service.ProductTradingHandler;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;

public record ExchangePacket(long worth) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ExchangePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "exchange"));

    public static final StreamCodec<ByteBuf, ExchangePacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, ExchangePacket::worth,
        ExchangePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ExchangePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                long balance = ProductTradingHandler.exchange(player, packet.worth);
                PacketDistributor.sendToPlayer(player, new UpdateVendingMachineScreenPacket(balance));
            }
        });
    }
}
