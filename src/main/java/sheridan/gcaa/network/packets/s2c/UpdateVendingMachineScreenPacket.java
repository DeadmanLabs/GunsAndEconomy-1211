package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record UpdateVendingMachineScreenPacket(long balance) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateVendingMachineScreenPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_vending_machine_screen"));

    public static final StreamCodec<ByteBuf, UpdateVendingMachineScreenPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, UpdateVendingMachineScreenPacket::balance,
        UpdateVendingMachineScreenPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateVendingMachineScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.updateVendingMachineScreen(packet.balance);
        });
    }
}
