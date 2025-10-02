package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.ModAttachments;
import sheridan.gcaa.capability.PlayerStatus;

public record SyncPlayerStatusPacket(long lastShoot, long lastChamberAction, long localTimeOffset, boolean reloading) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncPlayerStatusPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "sync_player_status"));

    public static final StreamCodec<ByteBuf, SyncPlayerStatusPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_LONG, SyncPlayerStatusPacket::lastShoot,
        ByteBufCodecs.VAR_LONG, SyncPlayerStatusPacket::lastChamberAction,
        ByteBufCodecs.VAR_LONG, SyncPlayerStatusPacket::localTimeOffset,
        ByteBufCodecs.BOOL, SyncPlayerStatusPacket::reloading,
        SyncPlayerStatusPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncPlayerStatusPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                PlayerStatus capability = player.getData(ModAttachments.PLAYER_STATUS);
                if (capability != null) {
                    capability.setLastShoot(packet.lastShoot);
                    capability.setLastChamberAction(packet.lastChamberAction);
                    capability.setReloading(packet.reloading);
                    capability.setLocalTimeOffset(packet.localTimeOffset);
                    capability.serverSetLatency(player);
                    capability.dataChanged = true;
                }
            }
        });
    }
}
