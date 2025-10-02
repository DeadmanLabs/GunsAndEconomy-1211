package sheridan.gcaa.network.packets.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.Clients;
import sheridan.gcaa.GCAA;

public record HeadShotFeedBackPacket(boolean isHeadshot) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HeadShotFeedBackPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "head_shot_feed_back"));

    public static final StreamCodec<ByteBuf, HeadShotFeedBackPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, HeadShotFeedBackPacket::isHeadshot,
        HeadShotFeedBackPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HeadShotFeedBackPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Clients.handleClientShotFeedBack(packet.isHeadshot);
        });
    }
}
