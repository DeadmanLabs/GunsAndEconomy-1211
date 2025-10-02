package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.vendingMachineProducts.VendingMachineProductsHandler;

public record UpdateVendingMachineProductsPacket(String strData, byte[] byteData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateVendingMachineProductsPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_vending_machine_products"));

    public static final StreamCodec<ByteBuf, UpdateVendingMachineProductsPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, UpdateVendingMachineProductsPacket::strData,
        ByteBufCodecs.BYTE_ARRAY, UpdateVendingMachineProductsPacket::byteData,
        UpdateVendingMachineProductsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateVendingMachineProductsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            VendingMachineProductsHandler.syncFromServer(packet.strData, packet.byteData);
        });
    }
}
