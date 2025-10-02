package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.capability.PlayerStatusProvider;
import sheridan.gcaa.network.packets.s2c.UpdateTransferBalancePacket;

import java.util.UUID;

public record TransferAccountsPacket(UUID currentPlayerId, long transferMoney) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TransferAccountsPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "transfer_accounts"));

    public static final StreamCodec<ByteBuf, TransferAccountsPacket> CODEC = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, TransferAccountsPacket::currentPlayerId,
        ByteBufCodecs.VAR_LONG, TransferAccountsPacket::transferMoney,
        TransferAccountsPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TransferAccountsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (player.getServer() != null) {
                    long balance = PlayerStatusProvider.getStatus(player).getBalance();
                    if (balance >= packet.transferMoney) {
                        // 转账给的玩家
                        ServerPlayer transferTo = player.getServer().getPlayerList().getPlayer(packet.currentPlayerId);
                        if (transferTo != null) {
                            // 被转账的玩家加钱
                            PlayerStatusProvider.getStatus(transferTo).serverSetBalance(PlayerStatusProvider.getStatus(transferTo).getBalance() + packet.transferMoney);
                            // 转账的玩家扣钱
                            PlayerStatusProvider.getStatus(player).serverSetBalance(PlayerStatusProvider.getStatus(player).getBalance() - packet.transferMoney);
                            PacketDistributor.sendToPlayer(player,
                                    new UpdateTransferBalancePacket(PlayerStatusProvider.getStatus(player).getBalance()));
                            PacketDistributor.sendToPlayer(transferTo,
                                    new UpdateTransferBalancePacket(PlayerStatusProvider.getStatus(transferTo).getBalance()));
                        }
                    }
                }
            }
        });
    }
}
