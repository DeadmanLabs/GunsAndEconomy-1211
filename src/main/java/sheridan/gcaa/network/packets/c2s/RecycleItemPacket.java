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
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;
import sheridan.gcaa.service.ProductTradingHandler;

public record RecycleItemPacket(int id) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RecycleItemPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "recycle_item"));

    public static final StreamCodec<ByteBuf, RecycleItemPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, RecycleItemPacket::id,
        RecycleItemPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RecycleItemPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                long balance = ProductTradingHandler.recycle(player, packet.id);
                PacketDistributor.sendToPlayer(player, new UpdateVendingMachineScreenPacket(balance));
            }
        });
    }
}
