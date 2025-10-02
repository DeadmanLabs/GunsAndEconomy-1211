package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.data.gun.GunPropertiesHandler;

import javax.annotation.Nullable;

public record UpdateGunPropertiesPacket(byte[] data, @Nullable String strData) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateGunPropertiesPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "update_gun_properties"));

    public static final StreamCodec<ByteBuf, UpdateGunPropertiesPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.BYTE_ARRAY, UpdateGunPropertiesPacket::data,
        ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), (packet) -> java.util.Optional.ofNullable(packet.strData),
        (data, strDataOpt) -> new UpdateGunPropertiesPacket(data, strDataOpt.orElse(null))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateGunPropertiesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            GunPropertiesHandler.syncFromServer(packet.data, packet.strData);
        });
    }
}
