package sheridan.gcaa.network.packets.c2s;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.s2c.UpdateVendingMachineScreenPacket;
import sheridan.gcaa.service.ProductTradingHandler;

public record BuyProductPacket(ItemStack itemStack, int productId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BuyProductPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "buy_product"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BuyProductPacket> CODEC = StreamCodec.composite(
        ItemStack.STREAM_CODEC, BuyProductPacket::itemStack,
        ByteBufCodecs.VAR_INT, BuyProductPacket::productId,
        BuyProductPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BuyProductPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    long balance = ProductTradingHandler.buy(player, packet.itemStack, packet.productId);
                    PacketDistributor.sendToPlayer(player, new UpdateVendingMachineScreenPacket(balance));
                }
            });
    }
}
