package sheridan.gcaa.network.packets.c2s;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import sheridan.gcaa.GCAA;
import sheridan.gcaa.items.ammunition.AmmunitionHandler;

public record AmmunitionManagePacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AmmunitionManagePacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(GCAA.MODID, "ammunition_manage"));

    public static final StreamCodec<ByteBuf, AmmunitionManagePacket> CODEC = StreamCodec.unit(new AmmunitionManagePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AmmunitionManagePacket packet, IPayloadContext context) {

            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    AmmunitionHandler.manageAmmunition(player, player.getMainHandItem());
                }
            });

    }
}
