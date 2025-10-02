package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record ClientSoundPacket(float originalVol, float volModify, float pitch, float x, float y, float z, String soundName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientSoundPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "client_sound"));

    public static final StreamCodec<ByteBuf, ClientSoundPacket> CODEC = new StreamCodec<>() {
        @Override
        public ClientSoundPacket decode(ByteBuf buf) {
            float originalVol = ByteBufCodecs.FLOAT.decode(buf);
            float volModify = ByteBufCodecs.FLOAT.decode(buf);
            float pitch = ByteBufCodecs.FLOAT.decode(buf);
            float x = ByteBufCodecs.FLOAT.decode(buf);
            float y = ByteBufCodecs.FLOAT.decode(buf);
            float z = ByteBufCodecs.FLOAT.decode(buf);
            String soundName = ByteBufCodecs.STRING_UTF8.decode(buf);
            return new ClientSoundPacket(originalVol, volModify, pitch, x, y, z, soundName);
        }

        @Override
        public void encode(ByteBuf buf, ClientSoundPacket packet) {
            ByteBufCodecs.FLOAT.encode(buf, packet.originalVol);
            ByteBufCodecs.FLOAT.encode(buf, packet.volModify);
            ByteBufCodecs.FLOAT.encode(buf, packet.pitch);
            ByteBufCodecs.FLOAT.encode(buf, packet.x);
            ByteBufCodecs.FLOAT.encode(buf, packet.y);
            ByteBufCodecs.FLOAT.encode(buf, packet.z);
            ByteBufCodecs.STRING_UTF8.encode(buf, packet.soundName);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ClientSoundPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.handleClientSound(packet.originalVol, packet.volModify, packet.pitch,
                    packet.x, packet.y, packet.z, packet.soundName);
        });
    }
}
