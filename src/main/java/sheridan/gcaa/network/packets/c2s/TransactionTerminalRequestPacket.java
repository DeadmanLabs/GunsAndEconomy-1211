package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.network.packets.s2c.UpdateTransactionDataPacket;

import java.util.ArrayList;

public record TransactionTerminalRequestPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TransactionTerminalRequestPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "transaction_terminal_request"));

    public static final StreamCodec<ByteBuf, TransactionTerminalRequestPacket> CODEC = StreamCodec.unit(new TransactionTerminalRequestPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TransactionTerminalRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.getServer() != null) {
                    PlayerList playerList = player.getServer().getPlayerList();
                    ArrayList<Integer> playerIds = new ArrayList<>();
                    playerList.getPlayers().forEach(p -> {
                        if (p.getId() != player.getId()) {
                            playerIds.add(p.getId());
                        }
                    });
                    PacketDistributor.sendToPlayer(player, new UpdateTransactionDataPacket(playerIds));
                }
            }
        });
    }
}
