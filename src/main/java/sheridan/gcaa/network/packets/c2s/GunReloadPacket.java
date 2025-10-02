package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.gun.IGun;

public record GunReloadPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GunReloadPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "gun_reload"));

    public static final StreamCodec<ByteBuf, GunReloadPacket> CODEC = StreamCodec.unit(new GunReloadPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GunReloadPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.getMainHandItem().getItem() instanceof IGun gun) {
                    gun.reload(player.getMainHandItem(), player);
                }
            }
        });
    }
}
